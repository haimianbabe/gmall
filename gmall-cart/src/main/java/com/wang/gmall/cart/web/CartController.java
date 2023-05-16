package com.wang.gmall.cart.web;

import com.wang.common.utils.R;
import com.wang.common.vo.CartItemVO;
import com.wang.common.vo.CartVO;
import com.wang.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    public String cartList(Model model) throws ExecutionException, InterruptedException {
        CartVO cartVO = cartService.cartList();
        model.addAttribute("cart",cartVO);
        return "cartList";
    }

    /**
     * 选中商品
     */
    @RequestMapping("/checkCart")
    public String checkCart(@RequestParam("skuId") Long skuId){
        cartService.checkCart(skuId);
        return "redirect:/cart/cartList";
    }

    /**
     * 修改购物车商品数量
     */
    @RequestMapping("/countItem")
    public String changeCount(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        cartService.changeCount(skuId,num);
        return "redirect:/cart/cartList";
    }

    /**
     * 删除购物项
     */
    @RequestMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:/cart/cartList";
    }

    /**
     * 获取购物车选中商品列表
     */
    @RequestMapping("/checkedList")
    @ResponseBody
    public List<CartItemVO> checkedList() throws ExecutionException, InterruptedException {
        CartVO cartVO = cartService.cartList();
        List<CartItemVO> items = cartVO.getItems();
        return items.stream().filter(item-> item.getCheck()).collect(Collectors.toList());
    }
}
