package com.darong.malgage_api.service.auth;

import com.darong.malgage_api.controller.dto.response.auth.TokenResponse;
import com.darong.malgage_api.domain.auth.RefreshToken;
import com.darong.malgage_api.domain.user.AuthProvider;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.domain.user.repository.UserRepository;
import com.darong.malgage_api.global.jwt.JwtProvider;
import com.darong.malgage_api.global.jwt.apple.AppleIdTokenVerifier;
import com.darong.malgage_api.repository.auth.RefreshTokenRepository;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppleLoginService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AppleIdTokenVerifier appleIdTokenVerifier;

    /**
     * iOS/Flutter에서 받은 idToken만으로 로그인 처리
     */
    @Transactional
    public TokenResponse login(String idToken) {
        SignedJWT jwt = appleIdTokenVerifier.verify(idToken);

        // Apple 클레임
        String oauthId = getStringClaim(jwt, "sub");      // 고유 식별자(필수)
        String email   = getStringClaim(jwt, "email");    // 최초동의 시에만 제공될 수 있음
        // 이름은 최초 한 번만 별도로 내려오며 id_token엔 없을 수 있음 (null 허용)
        String nickname = "Apple사용자";                  // 기본값 (앱에서 별도 수집 권장)
        String profileImage = null;                       // Apple은 프로필 이미지 미제공

        User user = userRepository.findByOauthIdAndProvider(oauthId, AuthProvider.APPLE)
                .orElseGet(() -> userRepository.save(
                        User.create(oauthId, AuthProvider.APPLE, email, nickname, profileImage)
                ));

        // Access/Refresh 발급 및 저장
        String accessToken = jwtProvider.createAccessToken(user.getEmail() != null ? user.getEmail() : user.getId().toString(), AuthProvider.APPLE, oauthId);
        String refreshToken = jwtProvider.createRefreshToken();

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        r -> r.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken))
                );

        return new TokenResponse(accessToken, refreshToken);
    }

    private String getStringClaim(SignedJWT jwt, String name) {
        try {
            var v = jwt.getJWTClaimsSet().getClaim(name);
            return v == null ? null : String.valueOf(v);
        } catch (Exception e) {
            return null;
        }
    }
}