package com.wang.gmall.product.web;

import com.wang.gmall.product.service.impl.SpuInfoServiceImpl;
import com.wang.gmall.product.vo.SkuItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("item")
public class ItemController {

    @Autowired
    SpuInfoServiceImpl spuInfoService;

    @RequestMapping("/{spuId}")
    public String itemInfo(@PathVariable("spuId") Long spuId,Model model) throws ExecutionException, InterruptedException {
        SkuItemVO skuItem = spuInfoService.getItemInfo(spuId);
        model.addAttribute("skuInfo",skuItem);
        return "item";
    }
}
