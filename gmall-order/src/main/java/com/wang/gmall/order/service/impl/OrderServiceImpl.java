package com.wang.gmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wang.common.constant.OrderConstant;
import com.wang.common.constant.OrderStatusEnum;
import com.wang.common.exception.NoStockException;
import com.wang.common.to.UserInfoTo;
import com.wang.common.utils.R;
import com.wang.gmall.order.config.LoginUserInterceptor;
import com.wang.gmall.order.entity.OrderItemEntity;
import com.wang.gmall.order.feign.CartFeignService;
import com.wang.gmall.order.feign.MemberFeignService;
import com.wang.gmall.order.feign.SkuFeignService;
import com.wang.gmall.order.feign.WareFeignService;
import com.wang.gmall.order.service.OrderItemService;
import com.wang.gmall.order.to.OrderCreateTo;
import com.wang.gmall.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    SkuFeignService skuFeignService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    ThreadPoolExecutor execute;

    @Autowired
    OrderItemService orderItemService;

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
            List<MemberAddressVo> memberAddressVos = memberFeignService.getAdressByUserId(userInfoTo.getUserId());
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
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);
        //验证防重令牌
        UserInfoTo userInfoTo = LoginUserInterceptor.threadLocal.get();
        String script= "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long execute = (Long) redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + userInfoTo.getUserId()), submitVo.getOrderToken());
        if (execute==0L){
            responseVo.setCode(1);
            return responseVo;
        }else {
            //创建订单、订单子项
            OrderCreateTo order =createOrderTo(userInfoTo,submitVo);
            //验证价格
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = submitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
            }else {
                //验价失败
                responseVo.setCode(2);
                return responseVo;
            }
            //保存订单
            //锁定库存
            List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map((item) -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setSkuId(item.getSkuId());
                orderItemVo.setCount(item.getSkuQuantity());
                return orderItemVo;
            }).collect(Collectors.toList());
            WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
            wareSkuLockVo.setLocks(orderItemVos);
            R r = wareFeignService.orderLockStock(wareSkuLockVo);
            //5.1 锁定库存成功
            if (r.get("code").equals("0")){
                responseVo.setOrder(order.getOrder());
                responseVo.setCode(0);
                return responseVo;
            }else {
                //5.2 锁定库存失败
                String msg = (String) r.get("msg");
                throw new NoStockException(msg);
            }
        }
    }

    //创建订单项，订单子项
    private OrderCreateTo createOrderTo(UserInfoTo userInfoTo, OrderSubmitVo submitVo) {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //生成订单号
        String orderSn = IdWorker.getTimeId();
        //构建订单
        OrderEntity entity = buildOrder(userInfoTo, submitVo,orderSn);
        orderCreateTo.setOrder(entity);
        //构建子订单
        List<OrderItemVo> cartItemVOS = cartFeignService.checkedList();
        List<OrderItemEntity> orderItemEntities = cartItemVOS.stream().map(order->{
            return buildOrderItems(order);
        }).collect(Collectors.toList());
        orderCreateTo.setOrderItems(orderItemEntities);
        //计算价格
        entity = compute(entity, orderItemEntities);
        orderCreateTo.setOrder(entity);
        orderCreateTo.setOrderItems(orderItemEntities);
        return orderCreateTo;
    }

    //构建订单
    private OrderEntity buildOrder(UserInfoTo userInfoTo, OrderSubmitVo submitVo, String orderSn) {
        OrderEntity orderEntity = new OrderEntity();
        //设置用户信息
        orderEntity.setMemberId(userInfoTo.getUserId());
        orderEntity.setMemberUsername(userInfoTo.getUserKey());
        //设置邮费与收件人信息
        BigDecimal fare = BigDecimal.valueOf(9);
        orderEntity.setFreightAmount(fare);
        MemberAddressVo address = memberFeignService.getById(submitVo.getAddrId());
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverRegion(address.getRegion());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        //设置订单相关状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setConfirmStatus(0);
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;
    }

    //构建子订单
    private OrderItemEntity buildOrderItems(OrderItemVo item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //sku信息
        Long skuId = item.getSkuId();
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttrValues(), ";"));
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        orderItemEntity.setSkuQuantity(item.getCount());
        //根据skuid查spu信息
        R r = skuFeignService.getSpuInfo(skuId);
        SpuInfoVo spuInfo = r.getData("spuinfo",new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(spuInfo.getId());
        orderItemEntity.setSpuName(spuInfo.getSpuName());
        orderItemEntity.setSpuBrand(spuInfo.getBrandName());
        orderItemEntity.setCategoryId(spuInfo.getCatalogId());
        //订单价格
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
        //实际价格：订单价格-优惠信息+邮费
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        BigDecimal realPrice = origin.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realPrice);
        return orderItemEntity;
    }

    //计算订单价格
    private OrderEntity compute(OrderEntity entity, List<OrderItemEntity> orderItemEntities) {
        //总价
        BigDecimal total = BigDecimal.ZERO;
        //优惠价格
        BigDecimal promotion=new BigDecimal("0.0");
        BigDecimal integration=new BigDecimal("0.0");
        BigDecimal coupon=new BigDecimal("0.0");
        //积分
        Integer integrationTotal = 0;
        Integer growthTotal = 0;

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            total=total.add(orderItemEntity.getRealAmount());
            promotion=promotion.add(orderItemEntity.getPromotionAmount());
            integration=integration.add(orderItemEntity.getIntegrationAmount());
            coupon=coupon.add(orderItemEntity.getCouponAmount());
            integrationTotal += orderItemEntity.getGiftIntegration();
            growthTotal += orderItemEntity.getGiftGrowth();
        }

        entity.setTotalAmount(total);
        entity.setPromotionAmount(promotion);
        entity.setIntegrationAmount(integration);
        entity.setCouponAmount(coupon);
        entity.setIntegration(integrationTotal);
        entity.setGrowth(growthTotal);

        //付款价格=商品价格+运费
        entity.setPayAmount(entity.getFreightAmount().add(total));

        //设置删除状态(0-未删除，1-已删除)
        entity.setDeleteStatus(0);
        return entity;
    }

    /**
     * 保存订单
     * @param orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity order = orderCreateTo.getOrder();
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());
        this.save(order);
        orderItemService.saveBatch(orderCreateTo.getOrderItems());
    }
}