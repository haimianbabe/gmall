package com.wang.gmall.search.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.wang.common.to.SkuEsModel;
import com.wang.common.utils.R;
import com.wang.gmall.search.config.GmallElasticSearch;
import com.wang.gmall.search.config.GmallElasticSearchConfig;
import feign.Client;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class TestController {

    @RequestMapping("/test")
    public void upData() throws IOException {
        RestHighLevelClient client = new GmallElasticSearch().client();
        IndexRequest indexRequest = new IndexRequest("skumodel");
        SkuEsModel skuEsModel = new SkuEsModel();
        skuEsModel.setSkuId(001L);
        skuEsModel.setSkuTitle("test");
        String json = JSON.toJSONString(skuEsModel);
        indexRequest.source(json, XContentType.JSON);
        IndexResponse index = client.index(indexRequest, GmallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }
}
