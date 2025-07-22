package com.darong.malgage_api.domain.category.dto;

import com.darong.malgage_api.domain.category.CategoryDefault;
import com.darong.malgage_api.domain.category.CategoryType;
import com.darong.malgage_api.domain.category.UserCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private CategoryType type;
    private boolean isDefault;

    // ✅ 기본 카테고리용 변환
    public static CategoryResponseDto from(CategoryDefault category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getType(),
                true
        );
    }

    // ✅ 사용자 카테고리용 변환
    public static CategoryResponseDto from(UserCategory category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getType(),
                false
        );
    }
}
