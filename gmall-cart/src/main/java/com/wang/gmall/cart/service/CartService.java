package com.wang.gmall.cart.service;

import com.wang.common.vo.CartItemVO;
import com.wang.common.vo.CartVO;
import org.springframework.data.redis.core.BoundHashOperations;

import java.util.concurrent.ExecutionException;

public interface CartService {

    BoundHashOperations<String, Object, Object> addCartItem(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVO getCartItem(Long skuId);

    CartVO cartList() throws ExecutionException, InterruptedException;

    void checkCart(Long skuId);

    void changeCount(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
