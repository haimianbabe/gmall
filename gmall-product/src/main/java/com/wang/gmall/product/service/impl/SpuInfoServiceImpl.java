package com.wang.gmall.product.service.impl;

import com.wang.common.to.SkuEsModel;
import com.wang.common.utils.R;
import com.wang.gmall.product.entity.*;
import com.wang.gmall.product.feign.EsProductFeign;
import com.wang.gmall.product.service.*;
import com.wang.gmall.product.vo.SkuItemSaleAttrVO;
import com.wang.gmall.product.vo.SkuItemVO;
import com.wang.gmall.product.vo.SpuItemAttrGroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.product.dao.SpuInfoDao;

import javax.annotation.Resource;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Resource
    private EsProductFeign esProductFeign;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Resource(name = "myThread")
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        //1、获取所有spu对应sku
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));

        //2、获取spu对应attr集合
        Map<Long, ProductAttrValueEntity> attrValueMap = productAttrValueService.baseAttrlistforspu(spuId)
                .stream().collect(Collectors.toMap(ProductAttrValueEntity::getAttrId, val->val));

        //3、查询允许被检索的属性并封装成attrEsModels
        List<Long> attrIds = attrService.selectAttrIds(new ArrayList<>(attrValueMap.keySet()));
        List<SkuEsModel.Attrs> attrEsModel = attrIds.stream().filter(attrId->attrId!=null).map(attrId->{
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            ProductAttrValueEntity productAttrValueEntity = attrValueMap.get(attrId);
            attrs.setAttrId(productAttrValueEntity.getAttrId());
            attrs.setAttrName(productAttrValueEntity.getAttrName());
            attrs.setAttrValue(productAttrValueEntity.getAttrValue());
            return attrs;
        }).collect(Collectors.toList());

        //todo 查询商品库存
        Map<Long,Boolean> hasStockMap =  null;

        //4、遍历sku封装
        List<SkuEsModel> upProducts = skuInfoEntities.stream().map(sku->{
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            //todo 封装库存
            skuEsModel.setHotScore(0L);
            BrandEntity brand = brandService.getById(sku.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(sku.getCatalogId());
            skuEsModel.setCatalogName(category.getName());
            skuEsModel.setAttrs(attrEsModel);
            return skuEsModel;
        }).collect(Collectors.toList());

        //5、调用es模块保存数据
        R ret = esProductFeign.esProductUp(upProducts);
        //todo 6、修改sku商品状态，上架状态
    }

    @Override
    public SkuItemVO getItemInfo(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVO skuItemVO = new SkuItemVO();
        CompletableFuture<SkuInfoEntity> getBaseInfo = CompletableFuture.supplyAsync(()->{
            //sku基本信息获取
            SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
            skuItemVO.setInfo(skuInfoEntity);
            return skuInfoEntity;
        },executor);

        CompletableFuture<String> getSaleAttrs = getBaseInfo.thenApplyAsync(entity->{
            //获取spu销售属性组合（颜色、内存）
            List<SkuItemSaleAttrVO> skuItemSaleAttrVOS = this.baseMapper.listSaleAttrs(entity.getSpuId());
            skuItemVO.setSaleAttr(skuItemSaleAttrVOS);
            return "成功获取spu销售属性组合";
        },executor);

        CompletableFuture<String> getSpuInfo = getBaseInfo.thenApplyAsync(entity->{
            //获取spu介绍(依赖1)
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(entity.getSpuId());
            skuItemVO.setDesc(spuInfoDescEntity);
            return "成功获取spu介绍";
        },executor);

        CompletableFuture<String> getAttrGroup = getBaseInfo.thenApplyAsync(entity->{
            //spu规格参数详情、规格参数分组
            List<SpuItemAttrGroupVO> spuItemAttrGroupVOS = this.baseMapper.getProductGroupAttrsBySpuId(entity.getSpuId(),entity.getCatalogId());
            skuItemVO.setGroupAttrs(spuItemAttrGroupVOS);
           return "成功获取spu规格参数详情、规格参数分组";
        },executor);

        //sku图片信息
        List<SkuImagesEntity> imagesEntities = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id",skuId));
        skuItemVO.setImages(imagesEntities);

        CompletableFuture.allOf(getSaleAttrs,getSpuInfo,getAttrGroup).get();

        return skuItemVO;
    }


}