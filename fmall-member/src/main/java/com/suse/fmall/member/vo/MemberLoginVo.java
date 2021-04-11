package com.suse.fmall.member.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author LiuJing
 * @Date: 2021/04/10/ 9:17
 * @Description
 */
@Data
@ToString
public class MemberLoginVo {
    private String loginacct;
    private String password;
}
