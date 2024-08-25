package com.munecting.api.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.global.auth.user.UserPrincipalDetails;
import com.munecting.api.global.error.exception.InternalServerException;
import com.munecting.api.global.error.exception.OidcException;
import com.munecting.api.global.error.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

import static com.munecting.api.global.common.dto.response.Status.*;
import static com.munecting.api.global.util.DecodeUtil.decodeBase64;

@Component
@Slf4j
public class JwtProvider {

    private final Key key;

    private final long access_token_expire_time;

    private final long refresh_token_expire_time;

    private final String authHeader;

    private final String prefix;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access.expiration}") long accessTokenExpireTime,
            @Value("${jwt.refresh.expiration}") long refreshTokenExpireTime,
            @Value("${spring.security.auth.header}") String authorization,
            @Value("${spring.security.auth.prefix}") String prefix,
            UserRepository userRepository,
            ObjectMapper objectMapper) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.access_token_expire_time = accessTokenExpireTime;
        this.refresh_token_expire_time = refreshTokenExpireTime;
        this.authHeader = authorization;
        this.prefix = prefix;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public String getIssueToken(Long userId, boolean isAccessToken) {
        if (isAccessToken) return generateToken(userId, access_token_expire_time);
        else return generateToken(userId, refresh_token_expire_time);
    }

    private String generateToken(Long userId, long tokenTime) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + tokenTime);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateAccessToken(String accessToken) {
        validateToken(accessToken, true);
    }

    public void validateRefreshToken(String refreshToken) {
        validateToken(refreshToken, false);
    }

    private void validateToken(String token, boolean isAccessToken) {
        try {
            getJwtParser().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            String tokenType = isAccessToken ? "access" : "refresh";
            log.warn("{} token expired: {}", tokenType, e.getMessage());
            throw new UnauthorizedException(isAccessToken ? EXPIRED_ACCESS_TOKEN : EXPIRED_REFRESH_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("unsupported JWT token: {}", e.getMessage());
            throw new UnauthorizedException(INVALID_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new UnauthorizedException(INVALID_TOKEN);
        }
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String accessToken) {
        Long userId = getSubject(accessToken);
        User user = userRepository.findById(userId).orElseThrow(() -> {
                log.warn("토큰을 발급받은 user의 회원 정보가 존재하지 않습니다.");
                throw new UnauthorizedException(INVALID_TOKEN);
        });
        UserPrincipalDetails principal = new UserPrincipalDetails(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    public Long getSubject(String token) {
        return Long.valueOf(getJwtParser()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }

    public Claims parseClaims(String token, PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("id token expired");
            throw new OidcException();
        } catch (RuntimeException e) {
            throw new InternalServerException();
        }
    }

    public Map<String, String> getHeader(String token) {
        String header = token.split("\\.")[0];

        try {
            return objectMapper.readValue(decodeBase64(header), Map.class);

        } catch (IOException e) {
            log.warn("Failed to parse token headers");
            throw new OidcException();
        }

    }

    public void validatePayload(String token, String issuer, String audience) {
        String payload = token.split("\\.")[1];

        try {
            Map payloadMap = objectMapper.readValue(decodeBase64(payload), Map.class);

            String issFromToken = (String) payloadMap.get("iss");
            validateIssue(issFromToken, issuer);

            String audFromToken = (String) payloadMap.get("aud");
            validateAudience(audFromToken, audience);

        } catch (IOException e) {
            log.warn("Failed to parse token payload");
            throw new OidcException();
        }
    }

    private void validateIssue(String issFromToken, String issuer) {
        if (issFromToken == null) {
            log.warn("Issuer claim from token is null");
            throw new OidcException();
        }
        if (!issFromToken.equals(issuer)) {
            log.warn("Invalid issuer: expected {}, but got {}.", issuer, issFromToken);
            throw new OidcException();
        }
    }

    private void validateAudience(String audFromToken, String audience) {
        if (audFromToken == null) {
            log.warn("Audience claim from token is null.");
        }
        if (!audFromToken.equals(audience)) {
            log.warn("Invalid audience: expected {}, but got {}.", audience, audFromToken);
            throw new OidcException();
        }
    }


}
