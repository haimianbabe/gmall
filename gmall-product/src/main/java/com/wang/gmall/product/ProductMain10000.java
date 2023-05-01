package com.wang.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.wang.gmall")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProductMain10000 {

    public static void main(String[] args) {

        SpringApplication.run(ProductMain10000.class,args);
    }
}