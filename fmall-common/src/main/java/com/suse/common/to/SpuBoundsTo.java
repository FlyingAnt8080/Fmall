package com.suse.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author LiuJing
 * @Date: 2021/03/12/ 21:53
 * @Description
 */

@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
