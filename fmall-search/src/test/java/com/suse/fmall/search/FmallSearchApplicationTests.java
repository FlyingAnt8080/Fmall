package com.suse.fmall.search;

import com.alibaba.fastjson.JSON;
import com.suse.fmall.search.config.FmallElasticSearchConfig;
import com.suse.fmall.search.service.ProductDeleteService;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;

@RunWith(SpringRunner.class)//2.2版本的SpringBoot测试要加
@SpringBootTest
class FmallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ProductDeleteService deleteService;

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    @Test
    public void testDelete(){
        try {
            boolean b = deleteService.productStatusDown(1L);
            System.out.println("删除：" + b);
        } catch (IOException e) {
            System.out.println("删除错误");
        }
    }

    /**
     * 测试存储数据到ES
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");//数据的id
//        indexRequest.source("userName","zhangsan","age",18,"gender","男");
        User user = new User();
        user.setUserName("Tom");
        user.setAge(22);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);//要保持的内容
        //执行操作
        IndexResponse index = client.index(indexRequest, FmallElasticSearchConfig.COMMON_OPTIONS);
        //提取有用的响应数据
        System.out.println(index);
    }
    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    /**
     * 按照年龄聚合，并且请求这些年龄段的这些人的平均薪资
     * @throws IOException
     */
    @Test
    public void searchData() throws IOException {
        //1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL，检索条件
        //builder 中封装的检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //1.1 构建检索条件
        builder.query(QueryBuilders.matchQuery("address","mill"));
//        builder.from();
//        builder.size();
        //1.2 按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        builder.aggregation(ageAgg);
        //1.3 计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        builder.aggregation(balanceAvg);

        searchRequest.source(builder);
        System.out.println("查询条件：" + builder.toString());
        //2.执行检索
        SearchResponse searchResponse = client.search(searchRequest, FmallElasticSearchConfig.COMMON_OPTIONS);
        //3.分析结果
        System.out.println(searchResponse.toString());
        //3.1 获取所有查到的数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String strSource= hit.getSourceAsString();
            Account account = JSON.parseObject(strSource, Account.class);
            System.out.println("acount:" + account);
        }

        //3.2 获取到这次检索的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
//        for (Aggregation aggregation : aggregations.asList()) {
//            System.out.println("当前聚合："+aggregation.getName());
//            aggregation.getName()
//        }
        Terms ageAggRes = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAggRes.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄：" + keyAsString + "==>" + bucket.getDocCount() + "人");
        }

        Avg balanceAggRes = aggregations.get("balanceAvg");
        System.out.println("平均薪资：" + balanceAggRes.getValue());
    }

    @Data
    @ToString
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }
}
