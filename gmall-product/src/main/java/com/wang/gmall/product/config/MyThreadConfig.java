package com.wang.gmall.product.config;

import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class MyThreadConfig {

    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        return new ThreadPoolExecutor(pool.getCoreSize(),pool.getMaxSize(),pool.getKeepAliveTime(), TimeUnit.SECONDS, (LinkedBlockingDeque<Runnable>) Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }
}
