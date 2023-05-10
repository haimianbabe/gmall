package com.wang.gmall.auth.feign;

import com.wang.common.utils.R;
import com.wang.common.vo.RegistVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "gmall-member")
@Service
public interface MemberFeignService {

    @RequestMapping("/member/member/regist")
    R rigist(@RequestBody RegistVO registVO);
}
