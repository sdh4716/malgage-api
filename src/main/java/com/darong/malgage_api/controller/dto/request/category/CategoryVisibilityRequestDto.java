package com.darong.malgage_api.controller.dto.request.category;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryVisibilityRequestDto {

    private Long categoryId;
    private Boolean visible;

    public CategoryVisibilityRequestDto(Long categoryId, Boolean visible) {
        this.categoryId = categoryId;
        this.visible = visible;
    }
}