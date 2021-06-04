package com.suse.fmall.cart.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.suse.common.constant.CartConstant;
import com.suse.common.utils.R;
import com.suse.fmall.cart.feign.ProductFeignService;
import com.suse.fmall.cart.interceptor.CartInterceptor;
import com.suse.fmall.cart.service.CartService;
import com.suse.fmall.cart.to.UserInfoTo;
import com.suse.fmall.cart.vo.Cart;
import com.suse.fmall.cart.vo.CartItem;
import com.suse.fmall.cart.vo.SkuInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author LiuJing
 * @Date: 2021/04/11/ 10:09
 * @Description
 */
@Service
public class CartServiceImpl implements CartService{
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor poolExecutor;

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String oldCartItemJson = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(oldCartItemJson)){
            //购物车无此商品
            //添加新商品
            //2.商品添加到购物车(异步任务)
            //(1) 远程查询商品的详细信息
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R res = productFeignService.getSkuInfo(skuId);
                SkuInfoVo skuInfo = res.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(skuInfo.getPrice());
            },poolExecutor);

            //(2)远程查询sku的组合信息
            CompletableFuture<Void> getSaleAttrValueTask = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValue = productFeignService.getSkuSaleAttrValue(skuId);
                cartItem.setSkuAttr(skuSaleAttrValue);
            }, poolExecutor);
            CompletableFuture.allOf(getSkuInfoTask,getSaleAttrValueTask).get();
            String cartItemJson = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(),cartItemJson);
            return cartItem;
        }else{
            //购物车有此商品
            CartItem cartItem = JSON.parseObject(oldCartItemJson,CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItemJson = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
        return cartItem;
    }

    /**
     * 获取购物车
     * @return
     */
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = null;
        //判断用户是否登录
        if (userInfoTo.getUserId() != null){
            //登录
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            //临时购物车的键
            String tempCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            //1.判断临时购物车中是否有数据
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            if (!CollectionUtils.isEmpty(tempCartItems)){
                //零时购物车有数据，需要合并
                for (CartItem cartItem : tempCartItems) {
                    addToCart(cartItem.getSkuId(),cartItem.getCount());
                }
                //清楚零时购物车数据
                clearCart(tempCartKey);
            }
            //2.获取登录后的购物车【包含合并过来的临时购物车的数据和登录后购物车的数据】
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }else {
            //未登录
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            //获取零时购物车的所有购物项
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        //清空购物车中被删除和被下架的商品
        List<CartItem> cartItems = clearDelOrDownSkuItem(cart.getItems(),cartKey);
        cart.setItems(cartItems);
        return cart;
    }

    //清楚被下架或被删除的商品项
    private List<CartItem> clearDelOrDownSkuItem(List<CartItem> items,String cartKey) {
        if (!CollectionUtils.isEmpty(items)){
            List<Long> skuIds = items.stream().map(CartItem::getSkuId).collect(Collectors.toList());
            List<Long> discardedSkuIds  = productFeignService.selectDelOrDownSkuIds(skuIds);
            discardedSkuIds.forEach(id-> deleteItem(id));
           return getCartItems(cartKey);
        }
        return items;
    }

    /**
     * 获取到要操作的购物车
     * 根据用户是否登录来获取不同的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey;
        //判断用户是否登录，来操作不同的购物车
        if (userInfoTo.getUserId() != null){
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        }else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        return hashOps;
    }

    /**
     * 根据key查询购物车中所有数据
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (!CollectionUtils.isEmpty(values)){
            List<CartItem> cartItems = values.stream().map((value) -> {
                String str = (String) value;
                CartItem cartItem = JSON.parseObject(str,CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItems;
        }
        return null;
    }

    /**
     * 清空购物车
     * @param cartKey
     */
    @Override
    public void clearCart(String cartKey){
        redisTemplate.delete(cartKey);
    }

    /**
     * 勾选购物项
     * @param skuId
     * @param check
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null){
            return null;
        }else {
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            //获取选中的购物项
            List<CartItem> checkedCartItems = cartItems.stream()
                    .filter(CartItem::getCheck)
                    .map((item)->{
                        //TODO 更新最新价格
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);
                        return item;
                    })
                    .collect(Collectors.toList());
            return checkedCartItems;
        }
    }

    @Override
    public void clearPurchasedCartItems(List<Long> ids) {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null){
            return;
        }else {
           String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
           BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
           ids.forEach((id)-> hashOps.delete(id.toString()));
        }
    }
}
