package com.wang.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mysql.cj.util.StringUtils;
import com.wang.common.to.UserInfoTo;
import com.wang.common.utils.R;
import com.wang.common.vo.CartItemVO;
import com.wang.common.vo.CartVO;
import com.wang.common.vo.SkuInfoVO;
import com.wang.gmall.cart.config.CartInterceptor;

import com.wang.gmall.cart.constant.CartConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService{

    public static final String CART_PREFIX = "cart:";

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    com.wang.gmall.cart.feign.skuFeignService skuFeignService;

    @Autowired
    ThreadPoolExecutor executor;


    @Override
    public BoundHashOperations<String, Object, Object> addCartItem(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        //数据在redis中存储的格式<String,<String,object>>
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        //在缓存中根据sku查找是否存在当前商品，若存在查找库存并加上数量
        String cartJson = (String) cartOps.get(skuId.toString());
        if (!StringUtils.isNullOrEmpty(cartJson)){
            CartItemVO cartVO = JSON.parseObject(cartJson, CartItemVO.class);
            cartVO.setCount(cartVO.getCount()+num);
            cartJson = JSON.toJSONString(cartVO);
            cartOps.put(skuId.toString(),cartJson);
            return cartOps;
        }else {
            CartItemVO cartVO = new CartItemVO();
            //若不存在，查找sku对应基本信息，再找到分组属性
            CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(()->{
                R skuInfo = skuFeignService.skuInfo(skuId);
                SkuInfoVO skuInfoVO = skuInfo.getData("skuInfo",new TypeReference<SkuInfoVO>(){});
                cartVO.setCheck(true);
                cartVO.setCount(num);
                cartVO.setImage(skuInfoVO.getSkuDefaultImg());
                cartVO.setPrice(skuInfoVO.getPrice());
                cartVO.setSkuId(skuId);
                cartVO.setTitle(skuInfoVO.getSkuTitle());
            },executor);
            //串行执行查找属性分组
            CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(()->{
                List<String> skuAttrs = skuFeignService.getSkuAttrs(skuId);
                cartVO.setSkuAttrValues(skuAttrs);
            },executor);
            CompletableFuture.allOf(completableFuture1,completableFuture2).get();
            cartJson = JSON.toJSONString(cartVO);
            cartOps.put(skuId.toString(),cartJson);
            return cartOps;
        }
    }

    /**
     * 获取商品项
     * @param skuId
     * @return
     */
    @Override
    public CartItemVO getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartJson = (String) cartOps.get(skuId.toString());
        CartItemVO cartItemVO = JSON.parseObject(cartJson, CartItemVO.class);
        return cartItemVO;
    }

    /**
     * 获取、合并购物车列表
     * @return
     */
    @Override
    public CartVO cartList() throws ExecutionException, InterruptedException {
        CartVO cartVO = new CartVO();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //获取临时购物车所有项
        List<CartItemVO> tempCart = getCartByKey(CART_PREFIX + userInfoTo.getUserKey());
        if (userInfoTo.getUserId()!=null){
            //若登录使用userid作为购物车名,并且将临时购物车合并，删除临时购物车
            if (tempCart!=null&&tempCart.size() > 0){
                for (CartItemVO item : tempCart){
                    addCartItem(item.getSkuId(),item.getCount());
                }
            }
            List<CartItemVO> userCartItems = getCartByKey(CART_PREFIX + userInfoTo.getUserId());
            cartVO.setItems(userCartItems);
            //清空购物车
            redisTemplate.delete(CART_PREFIX + userInfoTo.getUserKey());
            return cartVO;
        }else {
            //若没登录直接返回临时购物车
            cartVO.setItems(tempCart);
            return cartVO;
        }
    }

    /**
     * 选中购物项
     * @param skuId
     */
    @Override
    public void checkCart(Long skuId) {
        CartItemVO cartItemVO = getItemBySkuId(skuId);
        cartItemVO.setCheck(!cartItemVO.getCheck());
        getCartOps().put(skuId.toString(),JSON.toJSONString(cartItemVO));
    }

    /**
     * 修改商品数量
     * @param skuId
     * @param num
     */
    @Override
    public void changeCount(Long skuId, Integer num) {
        CartItemVO cartItemVO = getItemBySkuId(skuId);
        cartItemVO.setCount(num);
        getCartOps().put(skuId.toString(),JSON.toJSONString(cartItemVO));
    }

    /**
     * 删除购物项
     * @param skuId
     */
    @Override
    public void deleteItem(Long skuId) {
        getCartOps().delete(skuId.toString());
    }

    /**
     * 使用hash存储购物车内容，若用户登录使用userid作为key，若用户未登录使用userkey作为key
     * @return
     */
    private BoundHashOperations<String,Object,Object> getCartOps(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = null;
        if (userInfoTo!=null){
            if (userInfoTo.getUserId()!=null){
                cartKey = CART_PREFIX + userInfoTo.getUserId();
            }else {
                cartKey = CART_PREFIX + userInfoTo.getUserKey();
            }
        }
        return redisTemplate.boundHashOps(cartKey);
    }

    /**
     * 获取临时或用户购物车
     * @param key
     * @return
     */
    public List<CartItemVO> getCartByKey(String key){
        List<CartItemVO> cart = null;
        BoundHashOperations<String,Object,Object> cartOps = redisTemplate.boundHashOps(key);
        List<Object> cartItems = cartOps.values();
        if (cartItems!=null&cartItems.size()>0){
            cart = cartItems.stream().map(obj->{
                String item = (String) obj;
                return JSON.parseObject(item,CartItemVO.class);
            }).collect(Collectors.toList());
        }
        return cart;
    }

    /**
     * 根据skuid获取当前商品项
     */
    public CartItemVO getItemBySkuId(Long skuId){
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItem = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(cartItem, CartItemVO.class);
    }
}
