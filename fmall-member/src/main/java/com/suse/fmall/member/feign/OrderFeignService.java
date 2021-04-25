package com.suse.fmall.member.feign;

import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

/**
 * @Author LiuJing
 * @Date: 2021/04/19/ 21:00
 * @Description
 */
@FeignClient("fmall-order")
@Service
public interface OrderFeignService {
    @PostMapping("/order/order/listWithItems")
    R listWithItems(@RequestBody Map<String, Object> params);
}
