package com.suse.fmall.member.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author LiuJing
 * @Date: 2021/04/09/ 22:59
 * @Description
 */
@Data
@ToString
public class MemberRegisterVo {
    private String username;
    private String password;
    private String phone;
}
