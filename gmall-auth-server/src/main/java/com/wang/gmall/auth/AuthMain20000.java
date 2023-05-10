package com.wang.gmall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AuthMain20000 {

    public static void main(String[] args) {
        SpringApplication.run(AuthMain20000.class,args);
    }
}
