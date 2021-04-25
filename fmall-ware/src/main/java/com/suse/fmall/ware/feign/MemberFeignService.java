package com.suse.fmall.ware.feign;

import com.suse.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author LiuJing
 * @Date: 2021/04/15/ 23:33
 * @Description
 */
@FeignClient("fmall-member")
@Service
public interface MemberFeignService {
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
