package com.suse.fmall.order.vo;

import lombok.Data;

/**
 * @Author LiuJing
 * @Date: 2021/04/15/ 23:01
 * @Description
 */
@Data
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;
}
