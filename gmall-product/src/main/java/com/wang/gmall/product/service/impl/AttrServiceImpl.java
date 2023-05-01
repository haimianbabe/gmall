package com.wang.gmall.product.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.gmall.product.entity.AttrAttrgroupRelationEntity;
import com.wang.gmall.product.entity.AttrGroupEntity;
import com.wang.gmall.product.entity.ProductAttrValueEntity;
import com.wang.gmall.product.service.AttrAttrgroupRelationService;
import com.wang.gmall.product.service.AttrGroupService;
import com.wang.gmall.product.service.CategoryService;
import com.wang.gmall.product.vo.RespVO;
import org.springframework.beans.BeanUtils;
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

import com.wang.gmall.product.dao.AttrDao;
import com.wang.gmall.product.entity.AttrEntity;
import com.wang.gmall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    CategoryService categoryService;

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrGroupService attrGroupService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public PageUtils queryPage(Map<String, Object> params, String attrType, Long catId) {
        //1、根据attrType进行查询
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type","base".equalsIgnoreCase(attrType)?1:0);
        //2、如果带有分类id，查询分类
        if (catId!=0){
            queryWrapper.eq("catelog_id",catId);
        }
        //3、根据key查询模糊搜索
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> wrapper.eq("attr_id", key).or().like("attr_name", key));
        }
        //4、查询分页数据
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),queryWrapper);
        List<AttrEntity> records = page.getRecords();
        List<RespVO> respVOS = records.stream().map((entity)->{
            //4.1 查询属性基本信息
            RespVO respVO = new RespVO();
            BeanUtils.copyProperties(entity,respVO);
            //4.2 查询分类名并设置分类名称
            String cateName = categoryService.getById(entity.getCatelogId()).getName();
            respVO.setCatelogName(cateName);
            //4.3 判断是否是规格查询
            if ("base".equalsIgnoreCase(attrType)) {
                //4.3.1 查询参数分组关联关系
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",entity.getAttrId()));
                //4.3.2 根据分组id查分组名
                if (attrAttrgroupRelationEntity!=null&&attrAttrgroupRelationEntity.getAttrGroupId()!=null){
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
                    respVO.setAttrGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            return respVO;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(respVOS);

        return pageUtils;
    }

    @Transactional
    @Override
    public void saveAttr(RespVO respVO) {
        AttrEntity attr = new AttrEntity();
        BeanUtils.copyProperties(respVO,attr);
        this.save(attr);
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
//        AttrGroupEntity attrGroup = attrGroupService.getOne(new QueryWrapper<AttrGroupEntity>().eq("attr_group_name",respVO.getAttrGroupName()));
        attrAttrgroupRelationEntity.setAttrGroupId(respVO.getAttrGroupId());
        attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
    }

    @Override
    public List<Long> selectAttrIds(List<Long> attrKeys) {
       List<AttrEntity> attrEntities = this.listByIds(attrKeys);
       List<Long> ret = attrEntities.stream().map(entity->{
           if(entity.getSearchType()!=null && entity.getSearchType()==1){
               return entity.getAttrId();
           }
           return null;
       }).collect(Collectors.toList());
       return ret;
    }

}