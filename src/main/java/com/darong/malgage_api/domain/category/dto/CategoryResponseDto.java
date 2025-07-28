// domain/category/dto/CategoryResponseDto.java
package com.darong.malgage_api.domain.category.dto;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.CategoryScope;
import com.darong.malgage_api.domain.category.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private CategoryType type;
    private Integer sortOrder;
    private CategoryScope scope;
    private boolean isDefault;   // 플러터에서 쓰기 편하게
    private boolean isCustom;    // 플러터에서 쓰기 편하게
    private Long userId;         // 커스텀 카테고리의 경우 소유자 ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Category 엔티티에서 DTO로 변환
    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getSortOrder(),
                category.getScope(),
                category.isDefaultCategory(),    // scope == DEFAULT
                category.isCustomCategory(),     // scope == CUSTOM
                category.getUser() != null ? category.getUser().getId() : null,
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}