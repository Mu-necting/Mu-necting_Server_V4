package com.munecting.api.global.oauth2.dto;

import com.munecting.api.domain.user.dto.Role;
import com.munecting.api.domain.user.dto.SocialType;
import com.munecting.api.domain.user.entity.User;

import java.util.Map;
import java.util.UUID;

public class GoogleResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {

        this.attribute = attribute;
    }


    @Override
    public String getProviderId() {

        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }



    @Override
    public User toEntity(OAuth2Response oAuth2Response, SocialType socialType) {
        return User.builder()
                .socialType(socialType)
                .email(oAuth2Response.getProviderId())
                .nickname(String.valueOf(UUID.randomUUID()).substring(0, 8))
                .role(Role.USER)
                .build();
    }
}
