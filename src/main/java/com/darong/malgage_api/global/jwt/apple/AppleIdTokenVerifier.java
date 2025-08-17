package com.darong.malgage_api.global.jwt.apple;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AppleIdTokenVerifier {

    private final AppleJwksProvider jwksProvider;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.issuer}")
    private String issuer;

    /**
     * Apple ID Token(JWT) 서명 및 클레임 검증
     * - iss == https://appleid.apple.com
     * - aud == {clientId}
     * - exp 유효
     * - kid 매칭된 공개키로 서명 검증
     */
    public SignedJWT verify(String idToken) {
        try {
            SignedJWT jwt = SignedJWT.parse(idToken);

            // 1) kid/alg에 맞는 공개키 선택
            String kid = jwt.getHeader().getKeyID();
            JWK matching = jwksProvider.getJwkSet().getKeys().stream()
                    .filter(k -> kid.equals(k.getKeyID()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Apple 공개키(kid) 불일치"));

            // 2) 서명 검증
            JWSVerifier verifier = new RSASSAVerifier(matching.toRSAKey().toRSAPublicKey());
            if (!jwt.verify(verifier)) {
                throw new BadJWSException("Apple ID Token 서명 검증 실패");
            }

            // 3) 클레임 검증
            var claims = jwt.getJWTClaimsSet();
            if (!issuer.equals(claims.getIssuer())) {
                throw new IllegalArgumentException("iss 불일치");
            }
            if (!claims.getAudience().contains(clientId)) {
                throw new IllegalArgumentException("aud 불일치");
            }
            Date exp = claims.getExpirationTime();
            if (exp == null || exp.toInstant().isBefore(Instant.now())) {
                throw new IllegalArgumentException("토큰 만료");
            }

            return jwt;

        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException("유효하지 않은 Apple ID Token", e);
        } catch (BadJWSException e) {
            throw new RuntimeException(e);
        }
    }
}