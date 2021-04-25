package com.suse.fmall.seckill.feign;

import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author LiuJing
 * @Date: 2021/04/20/ 23:04
 * @Description
 */
@FeignClient("fmall-coupon")
@Service
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/latest3DaySession")
     R getLatest3DaySession();
}
