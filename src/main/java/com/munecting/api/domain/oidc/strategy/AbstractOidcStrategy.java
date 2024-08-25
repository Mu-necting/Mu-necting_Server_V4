package com.munecting.api.domain.oidc.strategy;

import com.munecting.api.domain.oidc.dto.OidcPublicKeyList;
import com.munecting.api.domain.oidc.keyManagement.PublicKeyProvider;
import com.munecting.api.domain.oidc.keyManagement.client.PublicKeyClient;
import com.munecting.api.global.auth.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import java.security.PublicKey;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractOidcStrategy implements OidcStrategy {

    protected final JwtProvider jwtProvider;
    protected final PublicKeyProvider publicKeyProvider;
    protected final PublicKeyClient publicKeyClient;
    protected final String issuer;
    protected final String audience;

    protected void validatePayload(String token) {
        jwtProvider.validatePayload(token, issuer, audience);
    }

    protected Map<String, String> getHeader(String idToken) {
        return jwtProvider.getHeader(idToken);
    }

    protected Claims getClaimsWithVerifySign(String idToken, Map<String, String> header) {
        PublicKey publicKey = generatePublicKey(publicKeyClient, header);
        Claims claims = jwtProvider.parseClaims(idToken, publicKey);
        return claims;
    }

    private PublicKey generatePublicKey(PublicKeyClient publicKeyClient, Map<String, String> header) {
        OidcPublicKeyList publicKeys = publicKeyClient.getPublicKeys();
        PublicKey publicKey = publicKeyProvider.generatePublicKey(header, publicKeys);
        return publicKey;
    }
}
