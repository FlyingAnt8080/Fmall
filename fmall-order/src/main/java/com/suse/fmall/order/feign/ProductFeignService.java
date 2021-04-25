package com.suse.fmall.order.feign;

import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author LiuJing
 * @Date: 2021/04/17/ 10:44
 * @Description
 */
@FeignClient("fmall-product")
@Service
public interface ProductFeignService {
    @GetMapping("/product/spuinfo/spuinfo/{skuId}")
    R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);
}
