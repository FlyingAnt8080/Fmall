package com.suse.fmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.suse.common.to.es.SkuEsModel;
import com.suse.fmall.search.config.FmallElasticSearchConfig;
import com.suse.fmall.search.constant.EsConstant;
import com.suse.fmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author LiuJing
 * @Date: 2021/03/21/ 22:01
 * @Description
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到es
        //1.给es建立索引，product
        //2.给es保存数据
        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String modelStr = JSON.toJSONString(skuEsModel);
            indexRequest.source(modelStr, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkRes = restHighLevelClient.bulk(bulkRequest, FmallElasticSearchConfig.COMMON_OPTIONS);
        //TODO 如果批量错误
        boolean success = !bulkRes.hasFailures();
        List<String> collect = Arrays.stream(bulkRes.getItems())
                .map(BulkItemResponse::getId).collect(Collectors.toList());
        log.info("商品上架完成：{},返回数据：{}",collect,bulkRes.toString());
        return success;
    }
}
