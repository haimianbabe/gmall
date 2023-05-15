package com.wang.gmall.order.service.impl;

import com.wang.gmall.order.vo.OrderConfirmVo;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.order.dao.OrderDao;
import com.wang.gmall.order.entity.OrderEntity;
import com.wang.gmall.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo getConfirmVo() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //查出所有商品
        //查出所有地址
        //库存
        //防重令牌
        return orderConfirmVo;
    }

}