package com.darong.malgage_api.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.darong.malgage_api.auth.security.UserPrincipal;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.domain.user.repository.UserRepository;
import com.darong.malgage_api.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration-minutes}")
    private long accessTokenExpirationMinutes;

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenExpirationDays;

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
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * accessTokenExpirationMinutes)) // 30분
                .sign(algorithm());
    }

    public String createRefreshToken() {
        return JWT.create()
                .withSubject("RefreshToken")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * refreshTokenExpirationDays)) // 14일
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
        User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("유저를 찾을 수 없습니다."));

        UserPrincipal userPrincipal = new UserPrincipal(userEntity);

        return new UsernamePasswordAuthenticationToken(userPrincipal, accessToken, userPrincipal.getAuthorities());
    }

}
