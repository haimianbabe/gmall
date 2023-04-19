package com.wang.gmall.product.service.impl;

import com.alibaba.druid.util.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.product.dao.AttrDao;
import com.wang.gmall.product.entity.AttrEntity;
import com.wang.gmall.product.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, String attrType, Long catId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type","base".equalsIgnoreCase(attrType)?0:1);
        if (catId!=0){
            queryWrapper.eq("catelog_id",catId);
        }
        String key = (String) params.get("key");
        //搜索的模糊查询
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> wrapper.eq("attr_id", key).or().like("attr_name", key));
        }
        return null;
    }

}