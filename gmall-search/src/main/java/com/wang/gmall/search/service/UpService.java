package com.wang.gmall.search.service;

import com.wang.common.to.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public interface UpService {

    public Boolean up(List<SkuEsModel> skuEsModels) throws IOException;
}
