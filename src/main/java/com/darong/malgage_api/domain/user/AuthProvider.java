package com.darong.malgage_api.domain.user;

import lombok.Getter;

@Getter
public enum AuthProvider {
    GOOGLE("구글"),
    APPLE("애플"),
    KAKAO("카카오");

    private final String displayName;

    AuthProvider(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 문자열로부터 AuthProvider 변환
     */
    public static AuthProvider fromString(String provider) {
        if (provider == null) {
            throw new IllegalArgumentException("인증 제공자는 필수입니다.");
        }

        try {
            return AuthProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 인증 제공자입니다: " + provider);
        }
    }
}