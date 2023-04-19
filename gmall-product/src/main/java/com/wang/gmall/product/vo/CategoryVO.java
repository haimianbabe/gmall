package com.wang.gmall.product.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wang.gmall.product.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

@Data
public class CategoryVO extends CategoryEntity {

    /**
     * 子节点
     */
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CategoryVO> childCategoryEntity;
}
