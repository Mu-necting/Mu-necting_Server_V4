package com.munecting.api.global.oauth2.dto;

import com.munecting.api.domain.user.dto.SocialType;
import com.munecting.api.domain.user.entity.User;

public interface OAuth2Response {

    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();
    String getEmail();
    //사용자 실명 (설정한 이름)
    User toEntity(OAuth2Response oAuth2Response, SocialType socialType);
}
