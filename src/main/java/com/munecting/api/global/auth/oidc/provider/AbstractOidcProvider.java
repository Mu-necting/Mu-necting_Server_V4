package com.munecting.api.global.auth.oidc.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munecting.api.global.auth.jwt.JwtProvider;
import com.munecting.api.global.auth.oidc.publicKey.client.PublicKeyClient;
import com.munecting.api.global.auth.oidc.publicKey.OidcPublicKeyList;
import com.munecting.api.global.auth.oidc.publicKey.PublicKeyProvider;
import com.munecting.api.global.error.exception.OidcException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Map;

import static com.munecting.api.global.util.DecodeUtil.decodeBase64;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractOidcProvider implements OidcProvider{
    protected final JwtProvider jwtProvider;
    protected final PublicKeyProvider publicKeyProvider;
    protected final PublicKeyClient publicKeyClient;

    protected PublicKey getPublicKey(PublicKeyClient publicKeyClient, String idToken) {
        OidcPublicKeyList publicKeys = publicKeyClient.getPublicKeys();
        PublicKey publicKey = publicKeyProvider.generatePublicKey(parseHeaders(idToken), publicKeys);
        return publicKey;
    }

    private Map<String, String> parseHeaders(String token) {
        String header = token.split("\\.")[0];
        try {
            return new ObjectMapper().readValue(decodeBase64(header), Map.class);
        } catch (IOException e) {
            log.warn("Failed to parse token headers");
            throw new OidcException();
        }
    }
}
