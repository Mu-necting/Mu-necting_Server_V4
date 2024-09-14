package com.munecting.api.domain.oidc.strategy.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.munecting.api.domain.oidc.dto.OidcUserInfo;
import com.munecting.api.domain.oidc.strategy.OidcStrategy;
import com.munecting.api.global.error.exception.OidcException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOidcStrategy implements OidcStrategy {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Override
    public OidcUserInfo authenticate(String idToken) {
        GoogleIdToken googleIdToken = verifyIdToken(idToken);

        String subject = googleIdToken.getPayload().getSubject();
        String email = googleIdToken.getPayload().getEmail();
        return OidcUserInfo.of(subject, email);
    }

    private GoogleIdToken verifyIdToken(final String idToken) {
        try {
            final GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);

            if (isNull(googleIdToken)) {
                throw new OidcException("Failed to verify google id token: token is null or invalid");
            }

            return googleIdToken;

        } catch (GeneralSecurityException | IOException e) {
            log.error("Error verifying Google ID token", e);
            throw new OidcException("An error occurred while verifying Google ID token");
        }
    }
}
