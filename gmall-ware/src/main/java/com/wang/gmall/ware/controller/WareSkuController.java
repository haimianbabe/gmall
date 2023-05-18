package com.wang.gmall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wang.common.exception.BizCodeEnum;
import com.wang.common.exception.NoStockException;
import com.wang.gmall.ware.vo.HasStockVo;
import com.wang.gmall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wang.gmall.ware.entity.WareSkuEntity;
import com.wang.gmall.ware.service.WareSkuService;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.R;



/**
 * 商品库存
 *
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 15:21:17
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 为某个订单锁定库存锁定库存
     * @param wareSkuLockVo
     * @return
     */
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo) {
        try {
            Boolean stock = wareSkuService.orderLockStock(wareSkuLockVo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }
    }

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/hosStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<HasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
        // 疑问：为啥hashMap返回泛型没有用
        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
