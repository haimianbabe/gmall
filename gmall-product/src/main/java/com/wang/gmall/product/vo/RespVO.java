package com.wang.gmall.product.vo;

import com.wang.gmall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

@Data
public class RespVO extends AttrEntity {

    //分组名
    private String attrGroupName;

    //分组id
    private Long attrGroupId;

    //分类名称
    private String CatelogName ;

    //分类完整路径
    private List<Long> catelogPath;
}
