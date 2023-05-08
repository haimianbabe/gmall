package com.wang.gmall.product.vo;

import com.wang.gmall.product.entity.SkuImagesEntity;
import com.wang.gmall.product.entity.SkuInfoEntity;
import com.wang.gmall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVO {
    //sku基本信息获取
    private SkuInfoEntity info;
    //sku图片信息
    private List<SkuImagesEntity> images;
    //获取spu销售属性组合（颜色、内存）
    private List<SkuItemSaleAttrVO> saleAttr;
    //获取spu介绍
    private SpuInfoDescEntity desc;
    //spu规格参数详情、规格参数分组
    private List<SpuItemAttrGroupVO> groupAttrs;
}
