package com.wang.gmall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class GmallSessionConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        // 指定session序列化到redis的序列化器
        return new GenericJackson2JsonRedisSerializer();
    }
}
