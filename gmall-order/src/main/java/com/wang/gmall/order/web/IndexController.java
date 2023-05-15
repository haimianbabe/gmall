package com.wang.gmall.order.web;

import com.wang.gmall.order.service.OrderService;
import com.wang.gmall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @Autowired
    OrderService orderService;

    @RequestMapping("/toConfirm")
    public String toConfirm(){
        return "confirm";
    }

    @RequestMapping("/toTrade")
    public String toTrade(Model model){
        OrderConfirmVo orderConfirmVo = orderService.getConfirmVo();
        model.addAttribute("confirmOrderData",orderConfirmVo);
        return "confirm";
    }
}
