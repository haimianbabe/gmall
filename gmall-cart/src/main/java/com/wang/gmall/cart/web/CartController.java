package com.wang.gmall.cart.web;

import com.wang.common.vo.CartItemVO;
import com.wang.common.vo.CartVO;
import com.wang.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@RequestMapping("/cart")
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 为防止刷新不断重复添加购物车，不直接跳转到成功页面
     * http://localhost:21000/cart/addCart?skuId=7&num=3
     */
    @RequestMapping("/addCart")
    public String addCartItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        //添加商品
        cartService.addCartItem(skuId, num);
        attributes.addAttribute("skuId",skuId);
        //将skuId添加至重定向
        return "redirect:/cart/success";
    }

    /**
     * 跳转至添加购物车成功页面
     * @return
     */
    @RequestMapping("/success")
    public String success(@RequestParam("skuId") Long skuId, Model model){
        CartItemVO cartVO = cartService.getCartItem(skuId);
        model.addAttribute("cartVO",cartVO);
        return "success.html";
    }

    /**
     * 获取购物车商品列表
     */
    @RequestMapping("/cartList")
    public String cartList(Model model){
        CartVO cartVO = cartService.cartList();
        model.addAttribute("cart",cartVO);
        return "cartList";
    }
}
