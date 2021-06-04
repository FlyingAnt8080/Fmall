package com.suse.fmall.cart.feign;

import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/11/ 16:03
 * @Description 远程调用商品服务
 */
@FeignClient("fmall-product")
@Service
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skuinfo/getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValue(@PathVariable("skuId") Long skuId);

    @PostMapping("/product/skuinfo/delOrDownSkuIds")
    List<Long> selectDelOrDownSkuIds(@RequestBody List<Long> skuIds);
}
