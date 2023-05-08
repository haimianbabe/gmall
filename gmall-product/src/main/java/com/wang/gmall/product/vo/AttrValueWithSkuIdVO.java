package com.wang.gmall.product.vo;

import lombok.Data;

@Data
public class AttrValueWithSkuIdVO {
    private String attrValue ;//属性值
    private String skuIds ;//该属性值对应的skuId的集合
}
