package com.wang.gmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.order.entity.OrderEntity;

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
}

