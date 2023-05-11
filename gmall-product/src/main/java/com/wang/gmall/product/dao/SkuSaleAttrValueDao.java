package com.wang.gmall.product.dao;

import com.wang.gmall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-30 16:23:08
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<String> getSkuAttrs(@Param("skuId") Long skuId);
}
