package com.wang.gmall.product.service.impl;

import com.wang.gmall.product.entity.BrandEntity;
import com.wang.gmall.product.service.BrandService;
import com.wang.gmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.product.dao.CategoryBrandRelationDao;
import com.wang.gmall.product.entity.CategoryBrandRelationEntity;
import com.wang.gmall.product.service.CategoryBrandRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveCatelog(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        String cateName = categoryService.getById(catelogId).getName();
        String brandName = brandService.getById(brandId).getName();
        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(cateName);
        this.save(categoryBrandRelation);
    }

    @Override
    public List<BrandEntity> listByCatId(Long catId) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = this.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",catId));
        List<BrandEntity> list = categoryBrandRelationEntities.stream().map((entity)->{
            return brandService.getById(entity.getBrandId());
        }).collect(Collectors.toList());
        return list;
    }

}