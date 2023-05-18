package com.wang.gmall.order.feign;

import com.wang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gmall-product")
@Service
public interface SkuFeignService {

    /**
     * 根据skuid获取spuinfo
     */
    @RequestMapping("product/skuinfo/getSpuInfo/{skuId}")
    R getSpuInfo(@PathVariable Long skuId);
}
