package com.wang.gmall.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.wang.gmall.product.entity.AttrAttrgroupRelationEntity;
import com.wang.gmall.product.entity.AttrEntity;
import com.wang.gmall.product.entity.CategoryEntity;
import com.wang.gmall.product.service.AttrAttrgroupRelationService;
import com.wang.gmall.product.service.AttrService;
import com.wang.gmall.product.service.CategoryService;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.product.dao.AttrGroupDao;
import com.wang.gmall.product.entity.AttrGroupEntity;
import com.wang.gmall.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    CategoryService categoryService;

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrService attrService;

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

    @Override
    public List<AttrEntity> attrRelation(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id",attrGroupId));
        List<AttrEntity> attrEntities = attrAttrgroupRelationEntities.stream().map((entity)->{
            AttrEntity attr = attrService.getById(entity.getAttrId());
            return attr;
        }).collect(Collectors.toList());
        return attrEntities;
    }

    @Override
    public PageUtils attrNoRelation(Map<String, Object> params,Long attrGroupId) {
        QueryWrapper<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityQueryWrapper = new QueryWrapper<>();
        attrAttrgroupRelationEntityQueryWrapper.notIn("attr_group_id",attrGroupId);
        IPage<AttrAttrgroupRelationEntity> pages = attrAttrgroupRelationService.page(new Query<AttrAttrgroupRelationEntity>().getPage(params),attrAttrgroupRelationEntityQueryWrapper);
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = pages.getRecords();
        List<AttrEntity> attrEntities = attrAttrgroupRelationEntities.stream().map((entity)->{
            AttrEntity attr = attrService.getById(entity.getAttrId());
            return attr;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(pages);
        pageUtils.setList(attrEntities);
        return pageUtils;
    }

    @Transactional
    @Override
    public void addRelation(List<Map<String, Long>> params) {
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        Long attrId = null;
        Long attrGroupId = null;
        for (Map<String, Long> param : params) {
            attrId =  param.get("attrId");
            attrGroupId =  param.get("attrGroupId");
            // 处理 attrId 和 attrGroupId
        }
        attrAttrgroupRelationEntity.setAttrId(attrId);
        attrAttrgroupRelationEntity.setAttrGroupId(attrGroupId);
        attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
    }

    public void getFullPath(Long parentId,List<Long> path){
        if(parentId!=0){
            CategoryEntity categoryEntity = categoryService.getById(parentId);
            path.add(parentId);
            getFullPath(categoryEntity.getParentCid(),path);
        }
    }

}