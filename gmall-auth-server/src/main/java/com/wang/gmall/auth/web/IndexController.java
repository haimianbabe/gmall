package com.wang.gmall.auth.web;

import com.wang.common.utils.R;
import com.wang.common.vo.RegistVO;
import com.wang.gmall.auth.feign.MemberFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    MemberFeignService memberFeignService;

//    @RequestMapping("/")
//    public String index(){
//        return "index";
//    }

    /**
     * 注册
     * @return
     */
    @RequestMapping("/regist")
    public R regist(@RequestBody RegistVO registVO){
        //1 判断校验结果是否通过
        //2 判断验证码是否通过，操作验证码
        //3 调用注册服务进行注册
        return memberFeignService.rigist(registVO);
    }
}
