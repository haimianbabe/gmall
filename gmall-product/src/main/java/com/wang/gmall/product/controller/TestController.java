package com.wang.gmall.product.controller;

import com.wang.common.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@RefreshScope
@Controller
@RequestMapping("test")
public class TestController {

    @Value("${spring.application.name}")
    private String app_name;

    @RequestMapping("/get")
    public R getAppName(){
        return R.ok().put("app_name",app_name);
    }
}
