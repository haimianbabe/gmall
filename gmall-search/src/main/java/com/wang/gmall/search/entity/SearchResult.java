package com.wang.gmall.search.entity;

import com.wang.common.to.SkuEsModel;
import com.wang.gmall.search.vo.AttrVo;
import com.wang.gmall.search.vo.BrandVo;
import com.wang.gmall.search.vo.CatalogVo;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    //查询到的所有商品信息
    private List<SkuEsModel> product;

    //当前页码
    private Integer pageNum;

    //总记录数
    private Long total;

    //总页码
    private Integer totalPages;
    //页码遍历结果集(分页)
    private List<Integer> pageNavs;

    //当前查询到的结果，所有涉及到的品牌
    private List<BrandVo> brands;

    //当前查询到的结果，所有涉及到的所有属性
    private List<AttrVo> attrs;

    //当前查询到的结果，所有涉及到的所有分类
    private List<CatalogVo> catalogs;
}
