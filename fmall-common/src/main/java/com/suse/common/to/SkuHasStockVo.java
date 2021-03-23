package com.suse.common.to;

import lombok.Data;

/**
 * @Author LiuJing
 * @Date: 2021/03/21/ 21:15
 * @Description
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;
}
