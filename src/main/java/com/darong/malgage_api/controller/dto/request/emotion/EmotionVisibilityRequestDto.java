package com.darong.malgage_api.controller.dto.request.emotion;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmotionVisibilityRequestDto {

    private Long emotionId;
    private Boolean visible;

    public EmotionVisibilityRequestDto(Long emotionId, Boolean visible) {
        this.emotionId = emotionId;
        this.visible = visible;
    }
}