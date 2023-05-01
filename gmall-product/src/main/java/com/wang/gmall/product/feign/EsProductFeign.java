package com.wang.gmall.product.feign;

import com.wang.common.to.SkuEsModel;
import com.wang.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "gmall-search")
@Service
public interface EsProductFeign {

    @PostMapping("/es/product/up")
    public R esProductUp(@RequestBody List<SkuEsModel> skuEsModels);

}
