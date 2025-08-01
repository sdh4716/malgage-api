// domain/category/dto/CategoryResponseDto.java
package com.darong.malgage_api.controller.dto.response.category;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.CategoryScope;
import com.darong.malgage_api.domain.category.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private CategoryType type;
    private Integer sortOrder;
    private CategoryScope scope;
    private String iconName;
    private boolean isDefault;   // 플러터에서 쓰기 편하게
    private boolean isCustom;    // 플러터에서 쓰기 편하게
    private Long userId;         // 커스텀 카테고리의 경우 소유자 ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isVisible;  // ← 여기에

    // 기존 변환 메서드 (가시성 없음)
    public static CategoryResponseDto from(Category category) {
        return of(category, null); // 기본값으로 isVisible null 처리
    }

    // ✅ 가시성 포함 변환 메서드
    public static CategoryResponseDto of(Category category, Boolean isVisible) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getSortOrder(),
                category.getScope(),
                category.getIconName(),
                category.isDefaultCategory(),
                category.isCustomCategory(),
                category.getUser() != null ? category.getUser().getId() : null,
                category.getCreatedAt(),
                category.getUpdatedAt(),
                isVisible // null or true/false
        );
    }

    /**
     * ✅ QueryDSL constructor projection 용
     */
    @QueryProjection
    public CategoryResponseDto(
            Long id,
            String name,
            CategoryType type,
            Integer sortOrder,
            CategoryScope scope,
            String iconName,
            boolean isDefault,
            boolean isCustom,
            Long userId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Boolean isVisible
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.sortOrder = sortOrder;
        this.scope = scope;
        this.iconName = iconName;
        this.isDefault = isDefault;
        this.isCustom = isCustom;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isVisible = isVisible;
    }
}