package com.wang.gmall.cart.service;

import org.springframework.data.redis.core.BoundHashOperations;

public interface CartService {

    BoundHashOperations<String, Object, Object> addCartItem(Long skuId, Integer num);
}
