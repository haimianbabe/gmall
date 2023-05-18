package com.wang.gmall.product.service.impl;

import com.wang.gmall.product.entity.SpuInfoEntity;
import com.wang.gmall.product.service.SpuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.product.dao.SkuInfoDao;
import com.wang.gmall.product.entity.SkuInfoEntity;
import com.wang.gmall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SpuInfoService spuInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public SpuInfoEntity getSpuInfo(Long skuId) {
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        return spuInfoService.getById(skuInfoEntity.getSpuId());
    }

}