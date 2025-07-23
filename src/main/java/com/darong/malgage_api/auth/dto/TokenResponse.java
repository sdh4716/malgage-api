package com.darong.malgage_api.auth.dto;

import lombok.Getter;

@Getter
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;

    // ✅ AccessToken만 있을 경우
    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
        this.refreshToken = null;
    }

    // ✅ Access + RefreshToken 모두 있을 경우
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
