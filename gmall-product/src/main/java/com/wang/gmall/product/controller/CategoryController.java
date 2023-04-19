package com.wang.gmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wang.gmall.product.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wang.gmall.product.entity.CategoryEntity;
import com.wang.gmall.product.service.CategoryService;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.R;



/**
 * 商品三级分类
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取商品分类树
     */
    @RequestMapping("/tree")
    public List<CategoryVO> getCategoryTree(){
        List<CategoryVO> categoryVOS = categoryService.listWithTree();
        return categoryVOS;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
//		categoryService.removeByIds(Arrays.asList(catIds));
        //TODO 判断被删数据是否被使用
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
