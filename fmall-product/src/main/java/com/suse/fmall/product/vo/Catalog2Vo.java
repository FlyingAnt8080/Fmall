package com.suse.fmall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/24/ 23:07
 * @Description
 *  二级分类vo
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalog2Vo {
    private String catalog1Id;//1级父分类id
    private List<Catalog3Vo> catalog3List;//三级子分类
    private String id;
    private String name;

    /**
     * 三级分类vo
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catalog3Vo{
        private String catalog2Id;//2级父分类id
        private String id;
        private String name;
    }
}
