package com.wang.gmall.member.feign;

import com.wang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gmall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/coupon/list")
    public R getCouponList();
}
