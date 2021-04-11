package com.suse.fmall.auth.feign;

import com.suse.common.utils.R;
import com.suse.fmall.auth.vo.SocialUser;
import com.suse.fmall.auth.vo.UserLoginVo;
import com.suse.fmall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author LiuJing
 * @Date: 2021/04/09/ 23:51
 * @Description
 */
@FeignClient("fmall-member")
@Component
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo vo);

    /**
     * 账号密码登录
     * @param loginVo
     * @return
     */
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo loginVo);

    /**
     * 社交登录
     * @param socialUser
     * @return
     */
    @PostMapping("/member/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUser socialUser);
}
