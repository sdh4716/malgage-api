package com.darong.malgage_api.domain.emotion;

import lombok.Getter;

@Getter
public enum EmotionScope {
    DEFAULT("기본"),
    CUSTOM("사용자정의");

    private final String displayName;

    EmotionScope(String displayName) {
        this.displayName = displayName;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public boolean isCustom() {
        return this == CUSTOM;
    }
}