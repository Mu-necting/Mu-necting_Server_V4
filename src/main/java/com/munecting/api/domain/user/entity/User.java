package com.munecting.api.domain.user.entity;

import com.munecting.api.domain.user.constant.Role;
import com.munecting.api.domain.user.constant.SocialType;
import com.munecting.api.global.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String socialId;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = true)
    private String profileImageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
}
