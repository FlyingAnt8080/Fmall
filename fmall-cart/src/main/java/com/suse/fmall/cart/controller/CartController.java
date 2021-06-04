package com.suse.fmall.cart.controller;

import com.suse.common.utils.R;
import com.suse.fmall.cart.service.CartService;
import com.suse.fmall.cart.vo.Cart;
import com.suse.fmall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author LiuJing
 * @Date: 2021/04/11/ 10:30
 * @Description
 */
@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 获取当前用户的购物车数据
     * @return
     */
    @GetMapping("/currentUserItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems(){
        return cartService.getUserCartItems();
    }

    /**
     * 删除购物车中已购买的商品项
     * @param ids
     * @return
     */
    @PostMapping("/clearPurchasedCartItems")
    @ResponseBody
    public R clearPurchasedCartItems(@RequestBody List<Long> ids){
        cartService.clearPurchasedCartItems(ids);
        return R.ok();
    }
    /**
     * 选择商品项
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.fmall.com/cart.html";
    }

    /**
     * 商品项目数量改变
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.fmall.com/cart.html";
    }

    /**
     * 根据skuId删除购物项
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.fmall.com/cart.html";
    }

    /**
     * 购物车页面
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        //1.快速得到用户信息,id,user-key
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     *
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        //将数据放到url后面
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.fmall.com/addToCartSuccess.html";
    }

    /**
     * 解决重复提交问题
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        //重定向到成功页面，再次查询购物车数据即可
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }
}
