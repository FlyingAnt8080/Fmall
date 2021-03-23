package com.suse.fmall.search.service;

import com.suse.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/03/21/ 21:59
 * @Description
 */

public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
