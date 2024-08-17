package com.munecting.api.global.auth.oidc.provider;

import com.munecting.api.global.auth.jwt.JwtProvider;
import com.munecting.api.global.auth.oidc.publicKey.PublicKeyProvider;
import com.munecting.api.global.auth.oidc.publicKey.client.ApplePublicKeyClient;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class AppleAuthProvider extends AbstractOidcProvider {

    public AppleAuthProvider(JwtProvider jwtProvider, PublicKeyProvider publicKeyProvider, ApplePublicKeyClient appleAuthClient) {
        super(jwtProvider, publicKeyProvider, appleAuthClient);
    }

    @Override
    public String getEmail(final String idToken) {
        PublicKey publicKey = getPublicKey(publicKeyClient, idToken);
        return jwtProvider.parseClaims(idToken, publicKey).get("email",String.class);
    }
}