package com.suse.fmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;


/**
 * @Author LiuJing
 * @Date: 2021/04/07/ 16:01
 * @Description 销售属性
 */
@Data
@ToString
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
