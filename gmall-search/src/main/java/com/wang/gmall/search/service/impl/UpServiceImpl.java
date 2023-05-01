package com.wang.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.wang.common.to.SkuEsModel;
import com.wang.gmall.search.config.GmallElasticSearch;
import com.wang.gmall.search.config.GmallElasticSearchConfig;
import com.wang.gmall.search.service.UpService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UpServiceImpl implements UpService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean up(List<SkuEsModel> skuEsModels) throws IOException {
        RestHighLevelClient client = new GmallElasticSearch().esRestClient();
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.id(skuEsModel.getSkuId().toString());
            String skuJson = JSON.toJSONString(skuEsModel);
            indexRequest.source(skuJson);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = client.bulk(bulkRequest, GmallElasticSearchConfig.COMMON_OPTIONS);
        Boolean b = bulk.hasFailures();
        List<String> errors = Arrays.stream(bulk.getItems()).map(item->{
            return item.getId();
        }).collect(Collectors.toList());
        log.error("上传出错：",errors);

        return b;


    }
}
