package com.wang.gmall.product.vo;

import com.wang.gmall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuItemAttrGroupVO {
    private String groupName;

    //attrId,attrName,attrValue
    private List<AttrVO> attrs;
}
