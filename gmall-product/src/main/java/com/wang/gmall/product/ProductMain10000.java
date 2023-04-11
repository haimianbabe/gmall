package com.wang.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductMain10000 {

    public static void main(String[] args) {

        SpringApplication.run(ProductMain10000.class,args);
    }
}