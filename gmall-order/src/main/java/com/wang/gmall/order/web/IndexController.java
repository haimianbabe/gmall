package com.wang.gmall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/toConfirm")
    public String toConfirm(){
        return "confirm";
    }
}
