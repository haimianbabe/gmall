package com.wang.gmall.product.dao;

import com.wang.gmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
