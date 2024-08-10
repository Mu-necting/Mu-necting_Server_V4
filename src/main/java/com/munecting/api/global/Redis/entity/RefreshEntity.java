package com.munecting.api.global.Redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@ToString
@Getter
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 604800000L) // 리프레시토큰과 expiretime 일치
public class RefreshEntity {
    @Id
    private String username;
    private String refreshToken;
    private String expiration;

}
