package com.wang.gmall.product.controller;

import java.util.Arrays;
import java.util.Map;


import com.wang.gmall.product.vo.AttrGroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wang.gmall.product.entity.AttrGroupEntity;
import com.wang.gmall.product.service.AttrGroupService;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.R;



/**
 * 属性分组
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catId}")
    public R listByCatId(@RequestParam Map<String, Object> params,@PathVariable("catId") Long catId){
        PageUtils page = attrGroupService.queryPage(params,catId);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long[] catlogPath = attrGroupService.getCategoryPath(attrGroup.getCatelogId());
        AttrGroupVO attrGroupVO = new AttrGroupVO();
        BeanUtils.copyProperties(attrGroup,attrGroupVO);
        attrGroupVO.setCatlogPath(catlogPath);
        return R.ok().put("attrGroup", attrGroupVO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
