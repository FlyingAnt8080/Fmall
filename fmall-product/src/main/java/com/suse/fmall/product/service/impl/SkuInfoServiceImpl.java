package com.suse.fmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.suse.common.utils.R;
import com.suse.fmall.product.entity.SkuImagesEntity;
import com.suse.fmall.product.entity.SpuInfoDescEntity;
import com.suse.fmall.product.entity.SpuInfoEntity;
import com.suse.fmall.product.feign.SearchFeignService;
import com.suse.fmall.product.feign.SeckillFeignService;
import com.suse.fmall.product.service.*;
import com.suse.fmall.product.vo.SeckillInfoVo;
import com.suse.fmall.product.vo.SkuItemSaleAttrVo;
import com.suse.fmall.product.vo.SkuItemVo;
import com.suse.fmall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;

import com.suse.fmall.product.dao.SkuInfoDao;
import com.suse.fmall.product.entity.SkuInfoEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SeckillFeignService seckillFeignService;
    @Autowired
    private SearchFeignService searchFeignService;
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w-> w.eq("sku_id",key).or().like("sku_name",key));
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)){
            wrapper.ge("price",min);
        }

        String max = (String) params.get("max");
        
        if (!StringUtils.isEmpty(max)){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(BigDecimal.ZERO) == 1){
                    wrapper.le("price",max);
                }
            }catch (Exception e){

            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
        return list;
    }

    /**
     * 用到异步编排来提升速度
     * @param skuId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public SkuItemVo getItemBySku(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1.查询sku基本信息 pms_sku_info
            SkuInfoEntity skuInfo = getById(skuId);
            skuItemVo.setSkuInfo(skuInfo);
            return skuInfo;
        }, threadPoolExecutor);

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2.查询sku图片信息 pms_spu_images
            List<SkuImagesEntity> skuImages = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setSkuImages(skuImages);
        });

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //3.获取spu的销售属性组合
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //4.获取spu的介绍
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfoDesc);
        }, threadPoolExecutor);


        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //5.获取spu的规格参数信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> seckillFuture = CompletableFuture.runAsync(() -> {
            //6.查询当前sku是否参与秒杀优惠
            R res = seckillFeignService.getSkuSeckillInfo(skuId);
            if (res.getCode() == 0) {
                SeckillInfoVo seckillInfo = res.getData(new TypeReference<SeckillInfoVo>() {});
                skuItemVo.setSeckillInfo(seckillInfo);
            }
        });
        //等待所有任务都完成
        CompletableFuture.allOf(imageFuture,saleAttrFuture,descFuture,baseAttrFuture,seckillFuture).get();
        return skuItemVo;
    }

    @Transactional
    @Override
    public void removeSkuInfoByIds(Long[] skuIds,boolean isDelEs) {
        //1.删除skuInfo
        this.removeByIds(Arrays.asList(skuIds));
        //2.删除对应的skuImages
        skuImagesService.removeBySkuIds(skuIds);
        //3.删除对应的销售属性
        skuSaleAttrValueService.removeBySkuIds(skuIds);
        //4.删除ES中对应的sku数据
        if (isDelEs){
           searchFeignService.deleteBySKuIds(skuIds);
        }
    }

    @Override
    public List<Long> selectDelOrDownSkuIds(List<Long> skuIds) {
        //查询skuIds中未被删除的skuId对应的skuInfo
        List<SkuInfoEntity> skuInfoEntities = this.listByIds(skuIds);
        //得到这些sku对应spu的spuId
        Set<Long> spuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSpuId).collect(Collectors.toSet());
        //查询spu信息
        List<SpuInfoEntity> spuInfoEntities = spuInfoService.listByIds(spuIds);
        //过滤出已下架的spu信息
        List<SpuInfoEntity> downEntities = spuInfoEntities.stream().filter(item -> item.getPublishStatus().equals(2)).collect(Collectors.toList());
        //skuIds中未被删除且未被下架的skuId
        List<Long> upSkuIds = skuInfoEntities.stream().filter((item) -> {
            for (SpuInfoEntity downEntity : downEntities) {
                if (downEntity.getId().equals(item.getSpuId())) return false;
            }
            return true;
        }).map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        skuIds.removeAll(upSkuIds);
        return skuIds;
    }
}