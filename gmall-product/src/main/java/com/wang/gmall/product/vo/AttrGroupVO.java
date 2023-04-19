package com.wang.gmall.product.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.wang.gmall.product.entity.AttrGroupEntity;
import com.wang.gmall.product.entity.BrandEntity;
import lombok.Data;

@Data
public class AttrGroupVO extends AttrGroupEntity {

    /**
     * 所属分类
     */
    @TableField(exist = false)
    private Long[] catlogPath;
}
