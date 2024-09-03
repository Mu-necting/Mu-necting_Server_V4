package com.munecting.api.domain.oidc.service;

import com.munecting.api.domain.oidc.factory.OidcFactory;
import com.munecting.api.domain.oidc.strategy.OidcStrategy;
import com.munecting.api.domain.oidc.dto.OidcUserInfo;
import com.munecting.api.domain.user.constant.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OidcService {

    private final OidcFactory oidcFactory;

    public OidcUserInfo getOidcUserInfo(SocialType socialType, String idToken) {
        OidcStrategy oidcStrategy = oidcFactory.getOidcStrategy(socialType);
        OidcUserInfo userInfo = oidcStrategy.authenticate(idToken);
        return userInfo;
    }
}
