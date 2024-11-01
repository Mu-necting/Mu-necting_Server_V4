package com.munecting.api.domain.user.entity;

import com.munecting.api.global.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Uuid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    private Long userId;

    public static Uuid toEntity(Long userId) {
        return Uuid.builder()
                .uuid(UUID.randomUUID().toString())
                .userId(userId)
                .build();
    }
}
