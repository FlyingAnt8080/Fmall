package com.suse.fmall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author LiuJing
 * @Date: 2021/05/05/ 15:25
 * @Description
 */
@Data
public class SpuInfoVo {
    /**
     * 商品id
     */
    private Long id;
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 分类id
     */
    private Long catalogId;
    /**
     * 所属分类名
     */
    private String catalogName;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 品牌名
     */
    private String brandName;
    /**
     *总量
     */
    private BigDecimal weight;
    /**
     * 上架状态[0 - 下架，1 - 上架 2 - 下架]
     */
    private Integer publishStatus;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *修改时间
     */
    private Date updateTime;

}
