package com.munecting.api.global.auth.oidc.provider;

import com.munecting.api.global.auth.jwt.JwtProvider;
import com.munecting.api.global.auth.oidc.publicKey.PublicKeyProvider;
import com.munecting.api.global.auth.oidc.publicKey.client.KakaoPublicKeyClient;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class KakaoAuthProvider extends AbstractOidcProvider {

    public KakaoAuthProvider(JwtProvider jwtProvider, PublicKeyProvider publicKeyProvider, KakaoPublicKeyClient publicKeyClient) {
        super(jwtProvider, publicKeyProvider, publicKeyClient);
    }

    @Override
    public String getEmail(String idToken) {
        PublicKey publicKey = getPublicKey(publicKeyClient, idToken);
        return jwtProvider.parseClaims(idToken, publicKey).get("email", String.class);
    }
}
