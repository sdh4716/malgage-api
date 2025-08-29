package com.darong.malgage_api.service.auth;

import com.darong.malgage_api.domain.auth.RefreshToken;
import com.darong.malgage_api.domain.user.*;
import com.darong.malgage_api.controller.dto.response.auth.TokenResponse;
import com.darong.malgage_api.repository.auth.RefreshTokenRepository;
import com.darong.malgage_api.domain.user.repository.UserRepository;
import com.darong.malgage_api.global.jwt.JwtProvider;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleLoginService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    // ✅ 허용할 Client ID 목록 (iOS / Android / Web)
    private static final List<String> CLIENT_IDS = Arrays.asList(
            "837504135906-1d05gslosi87e06nu6iksr8lbrkf6lfr.apps.googleusercontent.com", // iOS
            "837504135906-1hpkoj42u8h9gpb9a5l7792hsrp9jae9.apps.googleusercontent.com", // Android
            "837504135906-5jfc0rk2r5hout3mut32jh4qj421eiob.apps.googleusercontent.com"   // Web
    );

    @Transactional
    public TokenResponse login(String idTokenString) {
        GoogleIdToken.Payload payload = verifyIdToken(idTokenString);

        String oauthId = payload.getSubject();
        String email = payload.getEmail();
        String nickname = (String) payload.get("name");
        String profileImage = (String) payload.get("picture");

        User user = userRepository.findByOauthIdAndProvider(oauthId, AuthProvider.GOOGLE)
                .orElseGet(() -> userRepository.save(
                        User.create(oauthId, AuthProvider.GOOGLE, email, nickname, profileImage)
                ));

        // Access + Refresh Token 발급
        String accessToken = jwtProvider.createAccessToken(email, AuthProvider.GOOGLE, oauthId);
        String refreshToken = jwtProvider.createRefreshToken();

        // Refresh Token 저장 또는 갱신
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        r -> r.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken))
                );

        return new TokenResponse(accessToken, refreshToken);
    }

    private GoogleIdToken.Payload verifyIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(CLIENT_IDS) // ✅ 여러 Client ID 허용
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("유효하지 않은 Google ID Token");
            }

            return idToken.getPayload();

        } catch (Exception e) {
            throw new RuntimeException("구글 토큰 검증 실패", e);
        }
    }
}
