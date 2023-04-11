package com.wang.gmall.coupon.dao;

import com.wang.gmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 15:04:00
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
