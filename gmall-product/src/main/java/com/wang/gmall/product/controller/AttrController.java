package com.wang.gmall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.wang.gmall.product.vo.RespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wang.gmall.product.entity.AttrEntity;
import com.wang.gmall.product.service.AttrService;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.R;



/**
 * 商品属性
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 获取规格参数或销售属性
     */
    @RequestMapping("/{attrType}/list/{catId}")
    public R attrInfo(@RequestParam Map<String, Object> params,@PathVariable("attrType")String attrType,@PathVariable("catId")Long catId){
        PageUtils page = attrService.queryPage(params,attrType,catId);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrEntity attr = attrService.getById(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody RespVO attrVO){
//		attrService.save(attr);
        attrService.saveAttr(attrVO);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrEntity attr){
		attrService.updateById(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
