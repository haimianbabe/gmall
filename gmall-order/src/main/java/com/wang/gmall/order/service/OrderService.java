package com.wang.gmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.order.entity.OrderEntity;
import com.wang.gmall.order.vo.OrderConfirmVo;
import com.wang.gmall.order.vo.OrderSubmitVo;
import com.wang.gmall.order.vo.SubmitOrderResponseVo;

import java.util.Map;

/**
 * 订单
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 15:19:23
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo getConfirmVo();

    SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo);
}

