package com.suse.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/13/ 10:36
 * @Description
 * 满减信息
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
