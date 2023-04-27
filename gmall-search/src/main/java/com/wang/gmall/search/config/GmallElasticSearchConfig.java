package com.wang.gmall.search.config;

import org.elasticsearch.client.RequestOptions;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GmallElasticSearchConfig {
    public static final RequestOptions COMMON_OPTIONS;

    /**
     * 通用设置项
     */
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        //        builder.addHeader("Authorization", "Bearer " + TOKEN);
        //        builder.setHttpAsyncResponseConsumerFactory(
        //                new HttpAsyncResponseConsumerFactory
        //                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }
}
