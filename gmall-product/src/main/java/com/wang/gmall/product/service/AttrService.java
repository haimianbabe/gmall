package com.wang.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params,String attrType,Long catId);
}

