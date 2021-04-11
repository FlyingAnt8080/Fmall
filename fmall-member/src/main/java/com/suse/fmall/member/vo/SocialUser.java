package com.suse.fmall.member.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author LiuJing
 * @Date: 2021/04/10/ 13:56
 * @Description
 */
@Data
@ToString
public class SocialUser {
    private String access_token;
    private String remind_in;
    private Long expires_in;
    private String uid;
    private String isRealName;
}
