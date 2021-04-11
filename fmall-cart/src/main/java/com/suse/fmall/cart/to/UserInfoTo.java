package com.suse.fmall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @Author LiuJing
 * @Date: 2021/04/11/ 10:41
 * @Description
 */
@Data
@ToString
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false;
}
