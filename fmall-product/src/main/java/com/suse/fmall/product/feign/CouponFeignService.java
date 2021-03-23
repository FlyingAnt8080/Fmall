package com.suse.fmall.product.feign;

import com.suse.common.to.SkuReductionTo;
import com.suse.common.to.SpuBoundsTo;
import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author LiuJing
 * @Date: 2021/03/12/ 21:47
 * @Description
 */
@FeignClient("fmall-coupon")
@Service
public interface CouponFeignService {
    /**
     * 只要json数据模型是兼容的双方服务无需使用同一个To
     * @param spuBoundsTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    /**
     * 保存满减信息
     * @param skuReductionTo
     * @return
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
