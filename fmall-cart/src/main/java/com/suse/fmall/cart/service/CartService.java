package com.suse.fmall.cart.service;

import com.suse.fmall.cart.vo.Cart;
import com.suse.fmall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author LiuJing
 * @Date: 2021/04/11/ 10:09
 * @Description
 */
public interface CartService {
    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车中对应商品的购物项
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    /**
     * 获取购物车
     * @return
     */
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空购物车数据
     * @param cartKey
     */
    void clearCart(String cartKey);

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 修改购物项数量
     * @param skuId
     * @param num
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 根据skuId删除购物项
     * @param skuId
     */
    void deleteItem(Long skuId);

    List<CartItem> getUserCartItems();
}
