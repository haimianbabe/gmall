package com.wang.gmall.cart.feign;

import com.wang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gmall-product")
@Service
public interface skuFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R skuInfo(@PathVariable("skuId") Long skuId);

    @RequestMapping("product/skusaleattrvalue/getSkuAttrs/{skuId}")
    List<String> getSkuAttrs(@PathVariable("skuId") Long skuId);
}
