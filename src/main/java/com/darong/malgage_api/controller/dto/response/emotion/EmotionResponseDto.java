// domain/emotion/dto/EmotionResponseDto.java
package com.darong.malgage_api.controller.dto.response.emotion;

import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.EmotionScope;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EmotionResponseDto {
    private Long id;
    private String name;
    private Integer sortOrder;
    private EmotionScope scope;
    private String iconName;
    private boolean isDefault;   // 플러터에서 쓰기 편하게
    private boolean isCustom;    // 플러터에서 쓰기 편하게
    private Long userId;         // 커스텀 감정의 경우 소유자 ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isVisible;

    // ✅ Emotion 엔티티에서 DTO로 변환
    public static EmotionResponseDto from(Emotion emotion) {
        return of(emotion, null); // 기본값으로 isVisible null 처리
    }

    public static EmotionResponseDto of(Emotion emotion, Boolean isVisible) {
        return new EmotionResponseDto(
                emotion.getId(),
                emotion.getName(),
                emotion.getSortOrder(),
                emotion.getScope(),
                emotion.getIconName(),
                emotion.isDefaultEmotion(),    // scope == DEFAULT
                emotion.isCustomEmotion(),     // scope == CUSTOM
                emotion.getUser() != null ? emotion.getUser().getId() : null,
                emotion.getCreatedAt(),
                emotion.getUpdatedAt(),
                isVisible
        );
    }

    @QueryProjection
    public EmotionResponseDto(
            Long id,
            String name,
            Integer sortOrder,
            EmotionScope scope,
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