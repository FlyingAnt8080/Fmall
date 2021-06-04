package com.suse.fmall.search.service;

import java.io.IOException;

/**
 * @Author LiuJing
 * @Date: 2021/05/04/ 16:06
 * @Description
 */
public interface ProductDeleteService {
    boolean productStatusDown(Long spuId) throws IOException;

    boolean deleteBySKuIds(Long[] spuIds) throws IOException;
}
