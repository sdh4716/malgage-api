package com.darong.malgage_api.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String createAccessToken(String email) {
        return JWT.create()
                .withSubject("AccessToken")
                .withClaim("email", email)
                .withIssuedAt(new Date())
                //.withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30분
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60)) // 1분
                .sign(Algorithm.HMAC256(secret));
    }

    public String createRefreshToken() {
        return JWT.create()
                .withSubject("RefreshToken")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14)) // 14일
                .sign(Algorithm.HMAC256(secret));
    }


    // ✅ 리프레시 토큰 검증 로직
    public void validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm()).build();
            verifier.verify(token); // 유효성 + 만료 체크
        } catch (TokenExpiredException e) {
            throw new RuntimeException("Access Token이 만료되었습니다.", e);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Access Token 검증에 실패했습니다.", e);
        }
    }

    public String extractEmail(String token) {
        DecodedJWT decoded = JWT.require(algorithm()).build().verify(token);
        return decoded.getClaim("email").asString();
    }

}
