package com.wang.gmall.product.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.wang.gmall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wang.gmall.product.vo.SkuItemSaleAttrVO;
import com.wang.gmall.product.vo.SpuItemAttrGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 * 
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    List<SkuItemSaleAttrVO> listSaleAttrs(@Param("spuId")Long spuId);

    List<SpuItemAttrGroupVO> getProductGroupAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
