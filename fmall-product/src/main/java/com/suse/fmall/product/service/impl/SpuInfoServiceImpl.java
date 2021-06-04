package com.suse.fmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suse.common.constant.ProductConstant;
import com.suse.common.exception.BizCodeEnume;
import com.suse.common.to.SkuHasStockVo;
import com.suse.common.to.SkuReductionTo;
import com.suse.common.to.SpuBoundsTo;
import com.suse.common.to.es.SkuEsModel;
import com.suse.common.utils.R;
import com.suse.fmall.product.entity.*;
import com.suse.fmall.product.exception.SpuDownException;
import com.suse.fmall.product.exception.SpuUpException;
import com.suse.fmall.product.feign.CouponFeignService;
import com.suse.fmall.product.feign.SearchFeignService;
import com.suse.fmall.product.feign.WareFeginService;
import com.suse.fmall.product.service.*;
import com.suse.fmall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private WareFeginService wareFeginService;

    @Autowired
    private SearchFeignService searchFeginService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    /**
     * TODO 高级部分处理分布式事务
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1.保存spu信息：pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2.保存spu的描述图片：pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //3.保存spu的图片集：pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

        //4.保存spu的规格参数: pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(attrEntity.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        //5.保存spu积分信息：fmall-sms:sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds,spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        //调用远程服务
        R res = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (res.getCode() != 0){
            log.error("远程保存spu积分信息失败！");
        }
        //6.保存当前spu对应的所有sku信息
        List<Skus> skus = vo.getSkus();
        if (!CollectionUtils.isEmpty(skus)){
            skus.forEach(item -> {
                String defaultImg = "";
                //TODO 默认图片又问题，有时候添加不上
                for (Images img : item.getImages()) {
                    if(img.getDefaultImg() == 1){
                        defaultImg = img.getImgUrl();
                        break;
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                //private String skuName;
                //private BigDecimal price;
                //private String skuTitle;
                //private String skuSubtitle;
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //6.1) sku的基本信息：pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imageEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                    //过滤url为空的数据
                }).filter(img -> !StringUtils.isEmpty(img.getImgUrl())).collect(Collectors.toList());
                //6.2) sku的图片信息：pms_sku_images
                skuImagesService.saveBatch(imageEntities);
                //6.3) sku的销售属性信息：pms_sku_sale_attr_value
                List<Attr> attrs = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntites = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntites);

                //6.4) sku的优惠、满减等信息：fmall-sms -> sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //有满减或者优惠的时候才调用
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0){
                    R r  = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r.getCode() != 0){
                        log.error("远程保存sku优惠信息失败！");
                    }
                }
            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(SpuInfoQuery query) {
        IPage<SpuInfoVo> page = this.baseMapper.getSpuInfoByPage(new Page<>(query.getPage(), query.getLimit()), query);
        return new PageUtils(page);
    }

    @Override
    public void spuUp(Long spuId) throws SpuUpException {
        //1.查出当前spuId对应的所有sku信息，品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        // TODO 4.查出当前spu所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);
        Set<Long> idSet = new HashSet<>(searchAttrIds);

        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream()
                .filter(item -> idSet.contains(item.getAttrId()))
                .map(item -> {
                    SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs1);
                    return attrs1;
                })
                .collect(Collectors.toList());

        //2.封装每个sku的信息
        // TODO 1.发送远程调用，库存系统是否有库存
        List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Map<Long, Boolean> skusHasStockMap = null;
        try {
            R r = wareFeginService.getSkusStock(skuIds);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {};
            skusHasStockMap = r.getData(typeReference).stream()
                    .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        }catch (Exception e){
            log.error("库存查询服务异常：原因：{}",e);
        }
        Map<Long, Boolean> finalStockMap = skusHasStockMap;

        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            //组装需要的数据
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,esModel);
            //需要单独处理的字段
            //skuPrice,skuImg,hasStock,hotScore,brandName,brandImg,catalogName
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //hasStock,hotScore
            //设置库存信息
            if (finalStockMap == null){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // TODO 2.热度评分 0
            esModel.setHotScore(0L);
            // TODO 3.品牌和分类的名字信息
            BrandEntity brandEntity = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());

            //设置检索属性
            esModel.setAttrs(attrsList);
            return esModel;
        }).collect(Collectors.toList());
        // 5.将数据发送给ES进行保存：fmall-search
        R res = searchFeginService.productStatusUp(upProducts);
        if (res.getCode() == 0){
            //远程调用成功
            //6.修改当前spu状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.SpuStatusEnum.SPU_UP.getCode());
        }else {
            //商品上架失败
            throw new SpuUpException(BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        Long spuId = skuInfoEntity.getSpuId();
        SpuInfoEntity spuInfoEntity = getById(spuId);
        return spuInfoEntity;
    }

    /**
     * 商品下架
     * @param spuId
     */
    @Override
    public void spuDown(Long spuId) throws SpuDownException {
        R res = searchFeginService.productStatusDown(spuId);
        if (res.getCode() == 0){
             //修改状态
             baseMapper.updateSpuStatus(spuId,ProductConstant.SpuStatusEnum.SPU_DOWN.getCode());
        }else {
            throw new SpuDownException(BizCodeEnume.PRODUCT_DOWN_EXCEPTION.getMsg());
        }
    }

    @Transactional
    @Override
    public void removeBySpuId(Long id) {
        //1.删除SpuInfo信息
        this.removeById(id);
        //2.删除SpuImage信息
        spuImagesService.remove(new QueryWrapper<SpuImagesEntity>().eq("spu_id",id));
        //3.删除SpuDesc描述
        spuInfoDescService.removeById(id);
        //4.删除Spu对应的属性
        productAttrValueService.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",id));
        //5.删除Spu下的所有Sku相关信息
        List<SkuInfoEntity> spuInfos = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", id));
        if (!CollectionUtils.isEmpty(spuInfos)){
            List<Long> skuIds = spuInfos.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            skuInfoService.removeSkuInfoByIds(skuIds.toArray(new Long[]{}),false);
        }
    }
}