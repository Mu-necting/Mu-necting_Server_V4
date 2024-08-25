package com.munecting.api.domain.oidc.keyManagement.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaoPublicKeyClient extends PublicKeyClient {

    public KakaoPublicKeyClient(
            RestClient restClient,
            @Value("${spring.security.oauth.kakao.public-key-info}") String publicKeyUrl
    ) {
        super(restClient, publicKeyUrl);
    }
}
