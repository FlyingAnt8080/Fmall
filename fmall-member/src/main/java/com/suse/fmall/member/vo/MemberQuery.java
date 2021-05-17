package com.suse.fmall.member.vo;

import lombok.Data;

/**
 * @Author LiuJing
 * @Date: 2021/05/12/ 21:03
 * @Description
 */
@Data
public class MemberQuery {
    private String key;
    private Long page;
    private Long limit;
}
