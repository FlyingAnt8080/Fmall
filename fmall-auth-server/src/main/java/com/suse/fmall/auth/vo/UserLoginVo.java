package com.suse.fmall.auth.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author LiuJing
 * @Date: 2021/04/10/ 9:12
 * @Description
 */
@Data
@ToString
public class UserLoginVo {
    private String loginacct;
    private String password;
}
