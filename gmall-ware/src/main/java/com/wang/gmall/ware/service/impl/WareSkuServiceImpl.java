package com.wang.gmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.wang.common.constant.OrderStatusEnum;
import com.wang.common.constant.WareStatusEnum;
import com.wang.common.exception.NoStockException;
import com.wang.common.to.mq.OrderTo;
import com.wang.common.to.mq.StockDetailTo;
import com.wang.common.to.mq.StockLockedTo;
import com.wang.common.utils.R;
import com.wang.gmall.ware.entity.WareOrderTaskDetailEntity;
import com.wang.gmall.ware.entity.WareOrderTaskEntity;
import com.wang.gmall.ware.service.WareOrderTaskDetailService;
import com.wang.gmall.ware.service.WareOrderTaskService;
import com.wang.gmall.ware.vo.HasStockVo;
import com.wang.gmall.ware.vo.OrderItemVo;
import com.wang.gmall.ware.vo.SkuWareHasStock;
import com.wang.gmall.ware.vo.WareSkuLockVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.common.utils.PageUtils;
import com.wang.common.utils.Query;

import com.wang.gmall.ware.dao.WareSkuDao;
import com.wang.gmall.ware.entity.WareSkuEntity;
import com.wang.gmall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    WareSkuDao wareSkuDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 为某个订单锁定库存
     *
     * @param wareSkuLockVo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
        // 保存库存工作单的详情
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        // 按照下单的收获地址，找到一个就近仓库，锁定库存
        // 1、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> orderItemVos = wareSkuLockVo.getLocks();
        List<SkuWareHasStock> collect = orderItemVos.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪里有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());
        //锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有这个商品的库存
                throw new NoStockException(skuId.toString());
            }
            // 减库存
            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    //成功
                    skuStocked = true;
                    // 保存库存工作单详情
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                    wareOrderTaskDetailEntity.setSkuId(skuId);
                    wareOrderTaskDetailEntity.setSkuNum(hasStock.getNum());
                    wareOrderTaskDetailEntity.setTaskId(wareOrderTaskEntity.getId());
                    wareOrderTaskDetailEntity.setWareId(wareId);
                    wareOrderTaskDetailEntity.setLockStatus(WareStatusEnum.LOCK_WARE.getCode());
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);

                    //将库存锁定成功的消息发给消息队列
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    stockLockedTo.setDetail(stockDetailTo);
                    //告诉MQ库存锁定成功
                    //rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",stockLockedTo);
                    break;
                } else {
                    //失败，当前仓库锁定失败,重试下一个仓库
                }
            }
            if (skuStocked == false) {
                //当前商品所有仓库都没锁住
                throw new NoStockException(skuId.toString());
            }
        }
        // 全部锁定成功
        return true;
    }

    /**
     * 查看是否有库存
     * @param skuIds
     * @return
     */
    @Override
    public List<HasStockVo> getSkuHasStock(List<Long> skuIds) {
        return null;
    }

    /**
     * 解锁
     * @param stockLockedTo
     */
    public void unlock(StockLockedTo stockLockedTo) {
        StockDetailTo detailTo = stockLockedTo.getDetail();
        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detailTo.getId());
        //1.如果工作单详情不为空，说明该库存锁定成功
        if (detailEntity != null) {
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(stockLockedTo.getId());
            //根据OrderSn找到订单详情
            R r = orderFeignService.infoByOrderSn(taskEntity.getOrderSn());
            if (r.get("code") .equals("0")) {
                OrderTo order = r.getData("order", new TypeReference<OrderTo>() {
                });
                //没有这个订单||订单状态已经取消 解锁库存
                if (order == null||order.getStatus()== OrderStatusEnum.CANCLED.getCode()) {
                    //为保证幂等性，只有当工作单详情处于被锁定的情况下才进行解锁
//                    if (detailEntity.getLockStatus()== WareTaskStatusEnum.Locked.getCode()){
//                        unlockStock(detailTo.getSkuId(), detailTo.getSkuNum(), detailTo.getWareId(), detailEntity.getId());
//                    }
                }
            }else {
                throw new RuntimeException("远程调用订单服务失败");
            }
        }else {
            //无需解锁
        }
    }

}