package com.wang.gmall.product.service.impl;

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

import com.wang.gmall.product.dao.CategoryDao;
import com.wang.gmall.product.entity.CategoryEntity;
import com.wang.gmall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = categoryService.list();
        List<CategoryEntity> levelMenu = categoryEntities.stream()
                .filter(item -> item.getParentCid()==0)
                .map(menu->{
                    menu.setChildCategoryEntity(getChildCategory(menu,categoryEntities));
                    return menu;
                })
                .sorted((menu1,menu2)->{
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());
        return levelMenu;
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        categoryDao.deleteBatchIds(catIds);
    }


    public List<CategoryEntity> getChildCategory(CategoryEntity pCategory,List<CategoryEntity> allCategory){
        List<CategoryEntity> childMenu = allCategory.stream()
                .filter(item->{
                    return item.getParentCid().equals(pCategory.getCatId());
                })
                .map(menu->{
                    menu.setChildCategoryEntity(getChildCategory(menu,allCategory));
                    return menu;
                })
                .sorted((menu1,menu2)->{
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());
        return childMenu;
    }

}