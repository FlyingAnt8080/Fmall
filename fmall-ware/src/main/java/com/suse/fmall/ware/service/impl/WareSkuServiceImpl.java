package com.suse.fmall.ware.service.impl;

import com.suse.common.utils.R;
import com.suse.fmall.ware.feign.ProductFeignService;
import com.suse.fmall.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suse.common.utils.PageUtils;
import com.suse.common.utils.Query;
import com.suse.fmall.ware.dao.WareSkuDao;
import com.suse.fmall.ware.entity.WareSkuEntity;
import com.suse.fmall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
               queryWrapper
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.判断如果没有该库存记录就新增,存在就修改
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>()
                .eq("sku_id", skuId)
                .eq("ware_id", wareId));
        if (CollectionUtils.isEmpty(entities)){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku名字,如果失败，整个事务无需回滚
            //TODO 高级部分处理
            try{
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0){
                    Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }
            wareSkuDao.insert(wareSkuEntity);
        }else {
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            //查询当前sku的总库存量
            //SELECT SUM(stock - stock_locked) FROM `wms_ware_sku` WHERE sku_id = 1
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count == null ? false :count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

}