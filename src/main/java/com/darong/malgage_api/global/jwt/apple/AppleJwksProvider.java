package com.darong.malgage_api.global.jwt.apple;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Apple 공개키(JWKS) 공급자 - 5분 캐시
 */
@Component
@RequiredArgsConstructor
public class AppleJwksProvider {

    @Value("${apple.jwks-uri}")
    private String jwksUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicReference<CachedJwks> cache = new AtomicReference<>();

    public JWKSet getJwkSet() {
        CachedJwks cached = cache.get();
        if (cached != null && cached.expiresAt.isAfter(Instant.now())) {
            return cached.jwkSet;
        }

        try {
            // Apple은 CORS 허용, JSON 본문을 직접 읽거나 URL 로딩 둘 다 가능
            ResponseEntity<String> res = restTemplate.getForEntity(jwksUri, String.class);
            JWKSet set = JWKSet.parse(res.getBody());
            cache.set(new CachedJwks(set, Instant.now().plusSeconds(300))); // 5분 캐시
            return set;
        } catch (Exception e) {
            // 네트워크 실패 시, 마지막 캐시라도 있으면 사용 (탄력성)
            if (cached != null) return cached.jwkSet;
            throw new IllegalStateException("Apple JWKS를 가져오지 못했습니다.", e);
        }
    }

    private record CachedJwks(JWKSet jwkSet, Instant expiresAt) {}
}