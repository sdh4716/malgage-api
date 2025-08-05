package com.darong.malgage_api.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.darong.malgage_api.auth.RefreshToken;
import com.darong.malgage_api.auth.dto.TokenResponse;
import com.darong.malgage_api.auth.jwt.JwtProvider;
import com.darong.malgage_api.auth.repository.RefreshTokenRepository;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void logoutByRefreshToken(String refreshToken) {
        jwtProvider.validateToken(refreshToken);

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("해당 RefreshToken이 없습니다."));

        refreshTokenRepository.deleteByUserId(token.getUserId());
    }


    @Transactional
    public TokenResponse reissueAccessToken(String oldRefreshToken) {
        jwtProvider.validateToken(oldRefreshToken);
        DecodedJWT decoded = JWT.decode(oldRefreshToken);
        if (!"RefreshToken".equals(decoded.getSubject())) {
            throw new RuntimeException("잘못된 리프레시 토큰 타입입니다.");
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new RuntimeException("저장된 토큰이 없습니다."));

        User user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        String newAccessToken = jwtProvider.createAccessToken(user.getEmail());
        String newRefreshToken = jwtProvider.createRefreshToken();

        savedToken.updateToken(newRefreshToken);
        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
