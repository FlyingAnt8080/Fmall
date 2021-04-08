package com.suse.fmall.product.web;

import com.suse.fmall.product.service.SkuInfoService;
import com.suse.fmall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @Author LiuJing
 * @Date: 2021/04/07/ 14:45
 * @Description
 */
@Controller
public class ItemController {
    @Autowired
    private SkuInfoService skuInfoService;
    /**
     * 展示当前sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = skuInfoService.getItemBySku(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }
}
