package com.wang.gmall.order.feign;

import com.wang.common.utils.R;
import com.wang.gmall.order.vo.OrderItemVo;
import com.wang.gmall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gmall-ware")
@Service
public interface WareFeignService {

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hosStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);


    /**
     * 为某个订单锁定库存锁定库存
     * @param wareSkuLockVo
     * @return
     */
    @PostMapping("/ware/waresku/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo);

}
