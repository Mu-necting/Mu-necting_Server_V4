package com.munecting.api.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Configuration
@Profile("test")
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    private RedisServer redisServer;

    @PostConstruct
    public void startEmbeddedRedis() {
        redisServer = new RedisServer(port);
        redisServer.start();
    }

    @PreDestroy
    public void stopEmbeddedRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

}
