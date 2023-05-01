package com.wang.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.product.entity.AttrEntity;
import com.wang.gmall.product.entity.ProductAttrValueEntity;
import com.wang.gmall.product.vo.RespVO;

import java.util.List;
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

    void saveAttr(RespVO respVO);

    List<Long> selectAttrIds(List<Long> attrKeys);
}

