package com.wang.gmall.order.dao;

import com.wang.gmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 15:19:23
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
