package com.wang.gmall.order.web;

import com.wang.gmall.order.service.OrderService;
import com.wang.gmall.order.vo.OrderConfirmVo;
import com.wang.gmall.order.vo.OrderSubmitVo;
import com.wang.gmall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    /**
     * 提交订单成功，则携带返回数据转发至支付页面
     * 提交订单失败，则携带错误信息重定向至确认页
     * @return
     */
    @RequestMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, Model model, RedirectAttributes attributes){
        try{
            SubmitOrderResponseVo responseVo=orderService.submitOrder(submitVo);
            Integer code = responseVo.getCode();
            if (code==0){
                model.addAttribute("order", responseVo.getOrder());
                return "pay";
            }else {
                String msg = "下单失败;";
                switch (code) {
                    case 1:
                        msg += "防重令牌校验失败";
                        break;
                    case 2:
                        msg += "商品价格发生变化";
                        break;
                }
                attributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        }catch (Exception e){
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }
}
