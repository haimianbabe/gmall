package com.wang.gmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SearchMain12000 {

    public static void main(String[] args) {
        SpringApplication.run(SearchMain12000.class,args);
    }
}
