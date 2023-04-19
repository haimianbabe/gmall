package com.wang.gmall.product.service.impl;

import com.mysql.cj.util.StringUtils;
import com.wang.gmall.product.entity.CategoryEntity;
import com.wang.gmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.product.dao.AttrGroupDao;
import com.wang.gmall.product.entity.AttrGroupEntity;
import com.wang.gmall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isNullOrEmpty(key)){
            queryWrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
        if (catId!=0) {
            queryWrapper.eq("catelog_id", catId);
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public Long[] getCategoryPath(Long catId) {
        List<Long> path = new LinkedList<>();
        getFullPath(catId,path);
        Collections.reverse(path);
        Long[] ret = path.toArray(new Long[path.size()]);
        return ret;
    }

    public void getFullPath(Long parentId,List<Long> path){
        if(parentId!=0){
            CategoryEntity categoryEntity = categoryService.getById(parentId);
            path.add(parentId);
            getFullPath(categoryEntity.getParentCid(),path);
        }
    }

}