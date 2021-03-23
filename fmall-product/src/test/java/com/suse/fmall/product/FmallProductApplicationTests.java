package com.suse.fmall.product;


import com.suse.fmall.product.service.BrandService;
import com.suse.fmall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@Slf4j
@SpringBootTest
class FmallProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
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
}
