package com.wang.gmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.ware.entity.WareSkuEntity;
import com.wang.gmall.ware.vo.HasStockVo;
import com.wang.gmall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 15:21:17
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Boolean orderLockStock(WareSkuLockVo wareSkuLockVo);

    List<HasStockVo> getSkuHasStock(List<Long> skuIds);
}

