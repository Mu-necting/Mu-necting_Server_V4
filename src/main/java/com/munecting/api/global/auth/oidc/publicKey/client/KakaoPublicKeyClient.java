package com.munecting.api.global.auth.oidc.publicKey.client;

import com.munecting.api.global.auth.oidc.publicKey.OidcPublicKeyList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaoPublicKeyClient implements PublicKeyClient {

    private final RestClient restClient;
    private final String publicKeyUrl;

    public KakaoPublicKeyClient(
            RestClient restClient,
            @Value("${spring.security.oauth.kakao.public-key-info}") String publicKeyUrl
    ) {
        this.restClient = restClient;
        this.publicKeyUrl = publicKeyUrl;
    }

    public OidcPublicKeyList getPublicKeys() {
        return restClient.get()
                .uri(publicKeyUrl)
                .retrieve()
                .body(OidcPublicKeyList.class);
    }

}
