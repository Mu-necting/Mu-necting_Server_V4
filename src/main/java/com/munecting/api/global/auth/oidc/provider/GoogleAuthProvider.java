package com.munecting.api.global.auth.oidc.provider;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.InternalServerException;
import com.munecting.api.global.error.exception.OidcException;
import com.munecting.api.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleAuthProvider implements OidcProvider {

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Override
    public String getEmail(final String idToken) {
        return getGoogleIdToken(idToken).getPayload().getEmail();
    }

    private GoogleIdToken getGoogleIdToken(final String idToken) {
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

