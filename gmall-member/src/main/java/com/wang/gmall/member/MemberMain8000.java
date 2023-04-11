package com.wang.gmall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wang.gmall.member.feign")
public class MemberMain8000 {

    public static void main(String[] args) {

        SpringApplication.run(MemberMain8000.class,args);
    }
}