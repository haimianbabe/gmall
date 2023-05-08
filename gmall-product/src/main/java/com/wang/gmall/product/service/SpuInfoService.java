package com.wang.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.product.entity.SpuInfoEntity;
import com.wang.gmall.product.vo.SkuItemSaleAttrVO;
import com.wang.gmall.product.vo.SkuItemVO;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void up(Long spuId);

    SkuItemVO getItemInfo(Long skuId);

}

