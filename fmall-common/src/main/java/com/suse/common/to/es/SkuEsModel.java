package com.suse.common.to.es;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/21/ 17:28
 * @Description sku在ES中保存数据模型
 */
@Data
@ToString
public class SkuEsModel {

    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;
    private List<Attrs> attrs;

     @Data
     @ToString
     public static class Attrs{
         private Long attrId;
         private String attrName;
         private String attrValue;
     }
}
