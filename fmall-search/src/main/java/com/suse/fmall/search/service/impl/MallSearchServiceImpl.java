package com.suse.fmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.suse.common.constant.SearchConstant;
import com.suse.common.to.es.SkuEsModel;
import com.suse.common.utils.R;
import com.suse.fmall.search.config.FmallElasticSearchConfig;
import com.suse.fmall.search.constant.EsConstant;
import com.suse.fmall.search.feign.ProductFeignService;
import com.suse.fmall.search.service.MallSearchService;
import com.suse.fmall.search.vo.AttrResponseVo;
import com.suse.fmall.search.vo.BrandVo;
import com.suse.fmall.search.vo.SearchParam;
import com.suse.fmall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author LiuJing
 * @Date: 2021/03/29/ 17:10
 * @Description
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ProductFeignService productFeignService;
    /**
     * 去es检索
     * @param param 检索的所有参数
     * @return  返回检索结果
     */
    @Override
    public SearchResult search(SearchParam param) {
        SearchResult result = null;
        //1.动态构建出查询需要的DSL语句
        SearchRequest request = buildSearchRequest(param);
        //1.准备检索请求
        try {
            //2、执行检索请求
            SearchResponse response = client.search(request, FmallElasticSearchConfig.COMMON_OPTIONS);
            //3、分析响应数据封装成我们需要的格式
           result =  buildSearchResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 构建结果树
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {
        SearchResult result = new SearchResult();
        //1、返回的所有查询到的商品
        SearchHits hits = response.getHits();
        SearchHit[] hits1 = hits.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        if (hits1 != null && hits1.length > 0){
            for (SearchHit hit : hits1) {
                String skuEsStr = hit.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(skuEsStr, SkuEsModel.class);
                if (!StringUtils.isEmpty(param.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String skuTitleStr = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(skuTitleStr);
                }
                esModels.add(skuEsModel);
            }
        }
        result.setProducts(esModels);
        //2、当前所有商品涉到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            //得到属性的名字
            String attrName= ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //得到属性的值
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets()
                    .stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        //3、当前所有商品涉及到的所有品牌
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //得到品牌id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            //得到品牌的名字
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //得到品牌的图片
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        //4、当前所有商品涉及到的所有分类信息
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalogAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //得到分类id
            String catalogIdStr = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(catalogIdStr));
            //得到子聚合
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);

        //5、分页信息
        result.setPageNum(param.getPageNum());
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        int totalPage =  (int) (total % EsConstant.PRODUCT_PAGESIZE) == 0 ? (int) (total / EsConstant.PRODUCT_PAGESIZE) :(int) (total / EsConstant.PRODUCT_PAGESIZE) + 1;
        result.setTotalPages(totalPage);
        List<Integer> pageNavs = new ArrayList<>();
        for (int i=1;i<= totalPage;i++) pageNavs.add(i);
        result.setPageNavs(pageNavs);
        //6.构建面包屑导航
        if(!CollectionUtils.isEmpty(param.getAttrs())){
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr->{
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //attr=2_5寸:6寸
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                result.getAttrIds().add(Long.parseLong(s[0]));
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                if (r.getCode() == 0){
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {});
                    navVo.setNavName(data.getAttrName());
                }else {
                    navVo.setNavName(s[0]);
                }
                //2、取消了面包屑以后，要跳转的路径，将请求地址的URL里面的当前值空
                //拿到所有的查询条件
                String replace = replaceQueryString(param, attr,"attrs");
                navVo.setLink(SearchConstant.SEARCH_BASE_URL + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }
        //品牌
        if(!CollectionUtils.isEmpty(param.getBrandId())){
            List<SearchResult.NavVo> navVos = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            R r = productFeignService.brandInfo(param.getBrandId());
            if (r.getCode() == 0){
                List<BrandVo> brands = r.getData("brands", new TypeReference<List<BrandVo>>() {});
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (BrandVo brand : brands) {
                    buffer.append(brand.getBrandName()+";");
                    replace = replaceQueryString(param,brand.getBrandId()+"","brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink(SearchConstant.SEARCH_BASE_URL+replace);
            }
            navVos.add(navVo);
        }

        //TODO 分类 不需要导航取消

        return result;
    }

    /**
     * 替换字符串方法
     * @param param
     * @param value
     * @return
     */
    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = "";
        try {
           encode =  URLEncoder.encode(value,"UTF-8");
           encode.replace("+","%20");//浏览器对空格的编码和Java不一样
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param.get_queryString().replace("&"+key+"=" + encode, "");
    }

    /**
     * 准备检索请求
     * 模糊匹配，过滤(按照分类、品牌、库存、价格区间、属性)，排序、分页、高亮，聚合分析
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * 1、查询：模糊匹配，过滤(按照分类、品牌，价格区间、属性)
         */
        //1.构建bool-query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1、must 模糊匹配
        if (!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        //1.2、filter
        //1.2.1 按照三级分类查
        if (param.getCatalog3Id() != null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        //1.2.2 按照品牌的id查
        if (!CollectionUtils.isEmpty(param.getBrandId())){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        //1.2.3 按照库存查询
        if(param.getHasStock() == 1){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",true));
        }
        //1.2.4 按照价格区间
        //skuPrice=1_500/_500/500_
        if (!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String range = param.getSkuPrice();
            String[] s = range.split("_");
            if (range.startsWith("_")){
                rangeQuery.lt(s[1]);
            }else if (range.endsWith("_")){
                rangeQuery.gte(s[0]);
            }else {
                rangeQuery.gte(s[0]).lt(s[1]);
            }
            boolQuery.filter(rangeQuery);
        }
        //1.2.5 安装属性查询
        if (!CollectionUtils.isEmpty(param.getAttrs())){
            for (String attrStr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                //attrs=2_5寸:6寸
                String[] s = attrStr.split("_");
                String attrId = s[0];//检索属性id
                String[] attrValues = s[1].split(":");//该属性对应的值
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        //把所有查询条件进行封装
        sourceBuilder.query(boolQuery);
        /**
         * 2、排序、分页、高亮
         */
        //2.1 排序
        if (!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0],order);
        }
        //2.2 分页
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        //2.3 高亮
        if (!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 3.聚合分析
         */
        //1.品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        //TODO 聚合品牌信息 brand
        sourceBuilder.aggregation(brandAgg);
        //2.分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        //TODO 聚合分类信息 catalog
        sourceBuilder.aggregation(catalogAgg);
        //3.属性聚合
        //聚合出所有的attrID
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        //聚合分析出当前attrId对应的attrName
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //聚合出当前attrId所有的attrValue
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attrAgg.subAggregation(attrIdAgg);
        //TODO 聚合属性信息 attr
        sourceBuilder.aggregation(attrAgg);

        String DSLStr = sourceBuilder.toString();
        System.out.println(DSLStr);
        SearchRequest request = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return request;
    }
}
