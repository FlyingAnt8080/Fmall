package com.suse.fmall.product.vo;

import com.suse.fmall.product.entity.SkuImagesEntity;
import com.suse.fmall.product.entity.SkuInfoEntity;
import com.suse.fmall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/07/ 14:56
 * @Description
 */
@Data
public class SkuItemVo {
    //1.查询sku基本信息 pms_sku_info
    private SkuInfoEntity skuInfo;
    //2.查询sku图片信息 pms_spu_images
    private List<SkuImagesEntity> skuImages;
    //3.获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;
    //4.获取spu的介绍
    private SpuInfoDescEntity desc;
    //5.获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;
    private boolean hasStock = true;

}
