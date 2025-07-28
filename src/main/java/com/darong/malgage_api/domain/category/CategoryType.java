// domain/category/CategoryType.java
package com.darong.malgage_api.domain.category;

import lombok.Getter;

@Getter
public enum CategoryType {
    INCOME("수입"),
    EXPENSE("지출");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }

    /**
     * 문자열로부터 CategoryType 변환
     */
    public static CategoryType fromString(String type) {
        if (type == null) {
            throw new IllegalArgumentException("카테고리 타입은 필수입니다.");
        }

        try {
            return CategoryType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 타입입니다: " + type);
        }
    }
}