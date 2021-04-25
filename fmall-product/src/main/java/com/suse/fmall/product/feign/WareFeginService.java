package com.suse.fmall.product.feign;

import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/21/ 21:28
 * @Description
 */
@FeignClient("fmall-ware")
public interface WareFeginService {
    @PostMapping("/ware/waresku/hasstock")
    R getSkusStock(@RequestBody List<Long> skuIds);
}
