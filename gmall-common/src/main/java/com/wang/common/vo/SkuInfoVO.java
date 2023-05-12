package com.wang.common.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuInfoVO {

    /**
     * skuId
     */
    public Long skuId;
    /**
     * spuId
     */
    public Long spuId;
    /**
     * sku名称
     */
    public String skuName;
    /**
     * sku介绍描述
     */
    public String skuDesc;
    /**
     * 所属分类id
     */
    public Long catalogId;
    /**
     * 品牌id
     */
    public Long brandId;
    /**
     * 默认图片
     */
    public String skuDefaultImg;
    /**
     * 标题
     */
    public String skuTitle;
    /**
     * 副标题
     */
    public String skuSubtitle;
    /**
     * 价格
     */
    public BigDecimal price;
    /**
     * 销量
     */
    public Long saleCount;
}
