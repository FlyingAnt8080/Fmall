package com.suse.fmall.product.vo;

import lombok.Data;

/**
 * @Author LiuJing
 * @Date: 2021/05/05/ 15:39
 * @Description
 */
@Data
public class SpuInfoQuery {
    private Integer status;
    private String key;
    private Long brandId;
    private Long catelogId;
    private Long page;
    private Long limit;
}
