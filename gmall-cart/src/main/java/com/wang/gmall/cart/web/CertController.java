package com.wang.gmall.cart.web;

import com.wang.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/cart")
@Controller
public class CertController {

    @Autowired
    CartService cartService;

    /**
     * 为防止刷新不断重复添加购物车，不直接跳转到成功页面
     */
    @RequestMapping("/addCart")
    public String addCartItem(@RequestParam Long skuId, @RequestParam Integer num, RedirectAttributes attributes){
        //添加商品
        cartService.addCartItem(skuId,num);
        //将skuId添加至重定向
        return "redirect: http://localhost:21000/cart/success";
    }

    /**
     * 跳转至添加购物车成功页面
     * @return
     */
    @RequestMapping("/success")
    public String success(){
        return "success.html";
    }
}
