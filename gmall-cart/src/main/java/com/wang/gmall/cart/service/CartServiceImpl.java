package com.wang.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.mysql.cj.util.StringUtils;
import com.wang.common.to.UserInfoTo;
import com.wang.common.utils.R;
import com.wang.common.vo.CartVO;
import com.wang.common.vo.SkuInfoVO;
import com.wang.gmall.cart.config.CartInterceptor;
import com.wang.gmall.cart.feign.skuFeignService;
import jdk.internal.org.objectweb.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService{

    public static final String CART_PREFIX = "cart:";

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    com.wang.gmall.cart.feign.skuFeignService skuFeignService;


    @Override
    public BoundHashOperations<String, Object, Object> addCartItem(Long skuId, Integer num) {
        //数据在redis中存储的格式<String,<String,object>>
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        //在缓存中根据sku查找是否存在当前商品，若存在查找库存并加上数量
        String cartJson = (String) cartOps.get(skuId.toString());
        CartVO cartVO = null;
        if (!StringUtils.isNullOrEmpty(cartJson)){
            cartVO = (CartVO) JSON.parse(cartJson);
            cartVO.setCount(cartVO.getCount()+num);
            cartJson = JSON.toJSONString(cartVO);
            cartOps.put(skuId.toString(),cartJson);
            return cartOps;
        }else {
            //若不存在，查找sku对应基本信息，再找到分组属性
            R skuInfo = skuFeignService.skuInfo(skuId);
            SkuInfoVO skuInfoVO = (SkuInfoVO) skuInfo.get("skuInfo");
            cartVO.setCheck(true);
            cartVO.setCount(num);
            cartVO.setImage(skuInfoVO.getSkuDefaultImg());
            cartVO.setPrice(skuInfoVO.getPrice());
            cartVO.setSkuId(skuId);
            cartVO.setTitle(skuInfoVO.getSkuTitle());
            //todo 串行执行查找属性分组

        }


        //放入redis

        return cartOps;
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
        BoundHashOperations<String,Object,Object> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        return boundHashOperations;
    }
}
