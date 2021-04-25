package com.suse.fmall.order.feign;

import com.suse.fmall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/14/ 22:37
 * @Description
 */
@FeignClient("fmall-member")
@Service
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/addresses/{memberId}")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
