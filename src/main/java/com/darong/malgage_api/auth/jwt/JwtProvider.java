package com.darong.malgage_api.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.darong.malgage_api.auth.exception.TokenExpiredExceptionCustom;
import com.darong.malgage_api.auth.exception.TokenInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    private JWTVerifier verifier() {
        return JWT.require(algorithm()).build();
    }

    public String createAccessToken(String email) {
        return JWT.create()
                .withSubject("AccessToken")
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30분
                .sign(algorithm());
    }

    public String createRefreshToken() {
        return JWT.create()
                .withSubject("RefreshToken")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14)) // 14일
                .sign(algorithm());
    }

    /**
     * 모든 토큰의 유효성 확인 (만료 포함)
     */
    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm()).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }


    /**
     * AccessToken 전용 유효성 확인
     */
    public void validateAccessToken(String token) {
        DecodedJWT jwt = getDecodedJWT(token);
        if (!"AccessToken".equals(jwt.getSubject())) {
            throw new TokenInvalidException("AccessToken 형식이 아닙니다.");
        }
    }

    /**
     * RefreshToken 전용 유효성 확인
     */
    public void validateRefreshToken(String token) {
        DecodedJWT jwt = getDecodedJWT(token);
        if (!"RefreshToken".equals(jwt.getSubject())) {
            throw new TokenInvalidException("RefreshToken 형식이 아닙니다.");
        }
    }

    public String extractEmail(String token) {
        return getDecodedJWT(token).getClaim("email").asString();
    }

    public DecodedJWT getDecodedJWT(String token) {
        return verifier().verify(token);
    }

    /**
     * Spring Security 인증 객체 반환
     */
    public Authentication getAuthentication(String accessToken) {
        String email = extractEmail(accessToken);
        User userDetails = new User(email, "", Collections.emptyList()); // 권한은 따로 설정하지 않음
        return new UsernamePasswordAuthenticationToken(userDetails, accessToken, userDetails.getAuthorities());
    }
}
