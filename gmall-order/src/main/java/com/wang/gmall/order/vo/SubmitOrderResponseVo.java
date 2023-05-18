package com.wang.gmall.order.vo;

import com.wang.gmall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;

    /**
     * 错误状态码
     * 1:防重校验失败
     * 2:库存有误
     * **/
    private Integer code;
}
