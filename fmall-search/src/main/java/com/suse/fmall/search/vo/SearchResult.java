package com.suse.fmall.search.vo;

import com.suse.common.to.es.SkuEsModel;
import lombok.Data;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/29/ 17:37
 * @Description 查询返回Vo
 */
@Data
public class SearchResult {
    //查询的所有商品信息
    private List<SkuEsModel> products;
    //当前页面
    private Integer pageNum;
    //总记录数
    private Long total;
    //总页数
    private Integer totalPages;
    //页数列表
    private List<Integer> pageNavs;
    //当前涉及到的所有品牌
    private List<BrandVo> brands;
    //当前涉及到的所有属性
    private List<AttrVo> attrs;
    //当前涉及到的所有分类
    private List<CatalogVo> catalogs;
    //============以上是返回给页面的所有信息
    //面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();
    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
}
