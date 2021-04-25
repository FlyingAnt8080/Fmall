package com.suse.fmall.ware.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @Author LiuJing
 * @Date: 2021/04/17/ 14:24
 * @Description
 */
@Data
@ToString
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private Boolean locked;
}
