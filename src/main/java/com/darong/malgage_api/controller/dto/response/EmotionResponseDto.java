// domain/emotion/dto/EmotionResponseDto.java
package com.darong.malgage_api.domain.emotion.dto;

import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.EmotionScope;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmotionResponseDto {
    private Long id;
    private String name;
    private Integer sortOrder;
    private EmotionScope scope;
    private boolean isDefault;   // 플러터에서 쓰기 편하게
    private boolean isCustom;    // 플러터에서 쓰기 편하게
    private Long userId;         // 커스텀 감정의 경우 소유자 ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Emotion 엔티티에서 DTO로 변환
    public static EmotionResponseDto from(Emotion emotion) {
        return new EmotionResponseDto(
                emotion.getId(),
                emotion.getName(),
                emotion.getSortOrder(),
                emotion.getScope(),
                emotion.isDefaultEmotion(),    // scope == DEFAULT
                emotion.isCustomEmotion(),     // scope == CUSTOM
                emotion.getUser() != null ? emotion.getUser().getId() : null,
                emotion.getCreatedAt(),
                emotion.getUpdatedAt()
        );
    }
}