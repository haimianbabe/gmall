package com.wang.gmall.order.service.impl;

import com.wang.common.constant.OrderConstant;
import com.wang.common.to.UserInfoTo;
import com.wang.common.vo.CartItemVO;
import com.wang.gmall.order.config.LoginUserInterceptor;
import com.wang.gmall.order.feign.CartFeignService;
import com.wang.gmall.order.feign.MemberFeignService;
import com.wang.gmall.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.order.dao.OrderDao;
import com.wang.gmall.order.entity.OrderEntity;
import com.wang.gmall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    ThreadPoolExecutor execute;

    @Autowired
    RedisTemplate redisTemplate;

    public static final String USER_ORDER_TOKEN_PREFIX = "order:token:";

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 加载订单提交页时数据获取
     * @return
     */
    @Override
    public OrderConfirmVo getConfirmVo() {
        UserInfoTo userInfoTo = LoginUserInterceptor.threadLocal.get();
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //使用feign请求导致session丢失，获取session再重新设置
        //为SpingMVC中共享`request`数据的上下文，底层由`ThreadLocal`实现
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //feign查出所有已勾选商品
        CompletableFuture<Void> completableFuture1 = CompletableFuture.supplyAsync(()->{
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> cartItemVOS = cartFeignService.checkedList();
            orderConfirmVo.setItems(cartItemVOS);
            return cartItemVOS;
        },execute).thenAcceptAsync((checkedList)->{
            //feign查出库存遍历查出库存,设置所有商品均有库存
            List<Long> itemIds = checkedList.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            Map<Long,Boolean> hasStockMap = itemIds.stream().collect(Collectors.toMap(Long::valueOf,val->true));
            orderConfirmVo.setStocks(hasStockMap);
        },execute);

        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(()->{
            //feign查出所有地址
            List<MemberAddressVo> memberAddressVos = memberFeignService.getAdressById(userInfoTo.getUserId());
            orderConfirmVo.setMemberAddressVos(memberAddressVos);
        });
        //防重令牌
        String token = UUID.randomUUID().toString().replace("_","");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+userInfoTo.getUserId(),token,30, TimeUnit.SECONDS);
        orderConfirmVo.setOrderToken(token);
        try {
            CompletableFuture.allOf(completableFuture1,completableFuture2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return orderConfirmVo;
    }

    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo) {
        //验证防重令牌
        //创建订单、订单子项
        //验证价格
        //保存订单
        //锁定库存
        return null;
    }

}