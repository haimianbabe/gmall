package com.wang.gmall.order.feign;

import com.wang.gmall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@FeignClient("gmall-member")
public interface MemberFeignService {

    /**
     * 根据用户id获取地址
     */
    @RequestMapping("member/memberreceiveaddress/getById/{userId}")
    List<MemberAddressVo> getAdressById(@PathVariable("userId") String userId);
}
