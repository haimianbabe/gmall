package com.wang.gmall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WareMain11000 {

    public static void main(String[] args) {

        SpringApplication.run(WareMain11000.class,args);
    }
}