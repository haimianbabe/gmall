package com.wang.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.product.entity.CategoryEntity;
import com.wang.gmall.product.vo.CategoryVO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryVO> listWithTree();

    void removeMenuByIds(List<Long> catIds);

    List<CategoryEntity> getLevel1Cate();
}

