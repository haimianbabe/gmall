package com.wang.gmall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.wang.common.vo.SkuInfoVO;
import com.wang.gmall.product.entity.SpuInfoEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wang.gmall.product.entity.SkuInfoEntity;
import com.wang.gmall.product.service.SkuInfoService;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.R;



/**
 * sku信息
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-30 16:23:08
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 根据skuid获取spuinfo
     */
    @RequestMapping("/getSpuInfo/{skuId}")
    public R getSpuInfo(@PathVariable Long skuId){
        SpuInfoEntity spuInfoEntity = skuInfoService.getSpuInfo(skuId);
        return R.ok().put("spuinfo",spuInfoEntity);
    }

    /**
     * 获取skuInfo
     */
    @RequestMapping("/{skuId}")
    public R skuInfo(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        SkuInfoVO skuInfoVO = new SkuInfoVO();
        BeanUtils.copyProperties(skuInfo,skuInfoVO);
        return R.ok().put("skuInfo", skuInfoVO);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
