package com.wang.gmall.ware.dao;

import com.wang.gmall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品库存
 * 
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 15:21:17
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<Long> listWareIdHasSkuStock(Long skuId);

    Long lockSkuStock(Long skuId, Long wareId, Integer num);
}
