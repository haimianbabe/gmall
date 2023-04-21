package com.wang.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.product.entity.AttrEntity;
import com.wang.gmall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params,Long catId);

    Long[] getCategoryPath(Long catId);

    List<AttrEntity> attrRelation(Long attrGroupId);

    PageUtils attrNoRelation(Map<String, Object> params,Long attrGroupId);

    void addRelation(List<Map<String, Long>> params);
}

