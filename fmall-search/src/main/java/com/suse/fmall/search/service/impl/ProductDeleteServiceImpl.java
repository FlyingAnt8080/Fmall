package com.suse.fmall.search.service.impl;

import com.suse.fmall.search.config.FmallElasticSearchConfig;
import com.suse.fmall.search.constant.EsConstant;
import com.suse.fmall.search.service.ProductDeleteService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author LiuJing
 * @Date: 2021/05/04/ 16:10
 * @Description
 */
@Slf4j
@Service
public class ProductDeleteServiceImpl implements ProductDeleteService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    public boolean productStatusDown(Long spuId) throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(EsConstant.PRODUCT_INDEX);
        request.setQuery(new TermQueryBuilder("spuId",spuId));
        request.setConflicts("proceed");
        // 更新最大文档数
        request.setMaxDocs(1000);
        // 批次大小
        request.setBatchSize(1000);
        request.setSlices(2);
        // 使用滚动参数来控制“搜索上下文”存活的时间
        request.setScroll(TimeValue.timeValueMinutes(10));
        // 超时
        request.setTimeout(TimeValue.timeValueMinutes(2));
        // 刷新索引
        request.setRefresh(true);
        BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, FmallElasticSearchConfig.COMMON_OPTIONS);
        return response.getBulkFailures().size() > 0 ? false : true;
    }

    @Override
    public boolean deleteBySKuIds(Long[] spuIds) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < spuIds.length; i++) {
            bulkRequest.add(new DeleteRequest().index(EsConstant.PRODUCT_INDEX).id(spuIds[i].toString()));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, FmallElasticSearchConfig.COMMON_OPTIONS);
        return !bulk.hasFailures();
    }
}
