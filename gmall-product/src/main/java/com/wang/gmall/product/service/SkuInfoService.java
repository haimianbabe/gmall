package com.wang.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.common.utils.PageUtils;
import com.wang.gmall.product.entity.SkuInfoEntity;
import com.wang.gmall.product.vo.SkuItemVO;

import java.util.Map;

/**
 * sku信息
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-30 16:23:08
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

}

