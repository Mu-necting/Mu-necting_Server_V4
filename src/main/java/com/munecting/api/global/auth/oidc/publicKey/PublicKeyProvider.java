package com.munecting.api.global.auth.oidc.publicKey;

import com.munecting.api.global.auth.oidc.publicKey.OidcPublicKey;
import com.munecting.api.global.auth.oidc.publicKey.OidcPublicKeyList;
import com.munecting.api.global.error.exception.InternalServerException;
import com.munecting.api.global.error.exception.OidcException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import static com.munecting.api.global.util.DecodeUtil.decodeBase64;

@Component
@Slf4j
public class PublicKeyProvider {

    public PublicKey generatePublicKey(final Map<String, String> tokenHeaders, final OidcPublicKeyList publicKeys) {
        OidcPublicKey publicKey = publicKeys.getMatchedKey(tokenHeaders.get("kid"), tokenHeaders.get("alg"));
        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(final OidcPublicKey publicKey) {
        byte[] nBytes = decodeBase64(publicKey.n());
        byte[] eBytes = decodeBase64(publicKey.e());

        // n, e값을 이용해서 공개키 생성
        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, nBytes), new BigInteger(1, eBytes));
        try {
            return KeyFactory.getInstance(publicKey.kty()).generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Failed to generate public key from provided key specification. kty: {}, n: {}, e: {}",
                    publicKey.kty(), publicKey.n(), publicKey.e(), e);
            throw new OidcException();
        }
    }
}
