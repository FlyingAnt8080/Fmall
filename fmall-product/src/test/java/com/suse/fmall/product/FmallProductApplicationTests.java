package com.suse.fmall.product;


import com.suse.fmall.product.dao.AttrGroupDao;
import com.suse.fmall.product.dao.SkuSaleAttrValueDao;
import com.suse.fmall.product.service.BrandService;
import com.suse.fmall.product.service.CategoryService;
import com.suse.fmall.product.vo.SkuItemSaleAttrVo;
import com.suse.fmall.product.vo.SkuItemVo;
import com.suse.fmall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class FmallProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Resource
    AttrGroupDao attrGroupDao;
    @Resource
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Test

    public void test(){
//        List<SpuItemAttrGroupVo> attrGroup = attrGroupDao.getAttrGroupWithAttrsBySpuId(1l, 225l);
//        System.out.println(attrGroup);
        List<SkuItemSaleAttrVo> attrs = skuSaleAttrValueDao.getSaleAttrsBySpuId(1l);
        System.out.println(attrs);
    }
    @Test
    void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setBrandId(1L);
//        brandEntity.setName("华为");
////        brandEntity.setDescript("华为");
////        brandService.save(brandEntity);
//        brandService.updateById(brandEntity);
//        System.out.println("保存成功！");
//        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1));
//        list.forEach(System.out::println);

    }

    @Test
    public void testCatelogPath(){
        Long[] catelogPath = categoryService.findCatelogPath(225l);
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

    @Test
    public void testRedis(){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        //保存
        ops.set("key","hello "+ UUID.randomUUID().toString());
        //查询
        String key = ops.get("key");
        System.out.println("数据："+key);
    }

    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }
}
