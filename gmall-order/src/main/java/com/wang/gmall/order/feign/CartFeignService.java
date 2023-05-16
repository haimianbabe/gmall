package com.wang.gmall.order.feign;
import com.wang.common.vo.CartItemVO;
import com.wang.gmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;


@FeignClient("gmall-cart")
@Service
public interface CartFeignService {

    /**
     * 获取购物车选中商品列表
     */
    @RequestMapping("/cart/checkedList")
    List<OrderItemVo> checkedList();


}
