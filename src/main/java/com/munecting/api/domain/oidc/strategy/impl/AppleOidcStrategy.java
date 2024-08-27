package com.munecting.api.domain.oidc.strategy.impl;

import com.munecting.api.domain.oidc.dto.OidcUserInfo;
import com.munecting.api.domain.oidc.keyManagement.PublicKeyProvider;
import com.munecting.api.domain.oidc.keyManagement.client.KakaoPublicKeyClient;
import com.munecting.api.domain.oidc.strategy.AbstractOidcStrategy;
import com.munecting.api.global.auth.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AppleOidcStrategy extends AbstractOidcStrategy {
    public AppleOidcStrategy(
            JwtProvider jwtProvider,
            PublicKeyProvider publicKeyProvider,
            KakaoPublicKeyClient kakaoPublicKeyClient,
            @Value("${spring.security.oauth.apple.issuer}") String issuer,
            @Value("${spring.security.oauth.apple.audience}") String audience) {

        super(jwtProvider, publicKeyProvider, kakaoPublicKeyClient, issuer, audience);
    }

    @Override
    public OidcUserInfo authenticate(String idToken) {
        validatePayload(idToken);
        Map<String, String> header = getHeader(idToken);
        Claims claims = getClaimsWithVerifySign(idToken, header);

        return OidcUserInfo.of(claims.getSubject(), claims.get("email").toString());
    }
}
