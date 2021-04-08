package com.suse.fmall.search.service;

import com.suse.fmall.search.vo.SearchParam;
import com.suse.fmall.search.vo.SearchResult;

/**
 * @Author LiuJing
 * @Date: 2021/03/29/ 17:10
 * @Description
 */
public interface MallSearchService {
    /**
     * 根据参数检索
     * @param param 检索的所有参数
     * @return
     */
    SearchResult search(SearchParam param);
}
