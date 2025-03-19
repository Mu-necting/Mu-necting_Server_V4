package com.munecting.api.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@Configuration
@Profile("test")
public class EmbeddedRedisConfig {

    private RedisServer redisServer;
    private int redisPort;

    public EmbeddedRedisConfig() {
        this.redisPort = findAvailablePort();
    }

    @PostConstruct
    public void startEmbeddedRedis() {
        redisServer = new RedisServer(redisPort);
        try {
            redisServer.start();
        } catch (Exception e) {
            redisPort = findAvailablePort();
            redisServer = new RedisServer(redisPort);
            redisServer.start();
        }
    }

    @PreDestroy
    public void stopEmbeddedRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    private int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("포트 번호 할당 중 문제가 발생했습니다.", e);
        }
    }

}
