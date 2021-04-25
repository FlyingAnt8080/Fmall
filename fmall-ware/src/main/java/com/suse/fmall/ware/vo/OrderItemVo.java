package com.suse.fmall.ware.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/11/ 9:52
 * @Description 购物项内容
 */
@Data
@ToString
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
    private BigDecimal weight;
}
