package com.suse.fmall.product.feign;

import com.suse.common.utils.R;
import com.suse.fmall.product.fallback.SeckillFeignServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author LiuJing
 * @Date: 2021/04/22/ 22:27
 * @Description
 */
@FeignClient(value = "fmall-seckill",fallback = SeckillFeignServiceFallback.class)
public interface SeckillFeignService {

    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
