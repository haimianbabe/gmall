package com.wang.gmall.search.controller;

import com.wang.common.exception.BizCodeEnum;
import com.wang.common.to.SkuEsModel;
import com.wang.common.utils.R;
import com.wang.gmall.search.service.UpService;
import com.wang.gmall.search.service.impl.UpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/es/product")
public class ProductEsController {

    @Autowired
    private UpServiceImpl upService;

    @RequestMapping("/up")
    public R upProductEs(@RequestBody List<SkuEsModel> skuEsModels){
        Boolean b = null;
        try {
            b = upService.up(skuEsModels);
        } catch (IOException e) {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (b){
            return R.ok();
        }
        return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
    }
}
