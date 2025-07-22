package com.darong.malgage_api.domain.emotion.dto;

import com.darong.malgage_api.domain.emotion.EmotionDefault;
import com.darong.malgage_api.domain.emotion.UserEmotion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmotionResponseDto {
    private Long id;
    private String name;
    private boolean isDefault;

    public static EmotionResponseDto from(EmotionDefault emotion) {
        return new EmotionResponseDto(
                emotion.getId(),
                emotion.getName(),
                true
        );
    }

    public static EmotionResponseDto from(UserEmotion emotion) {
        return new EmotionResponseDto(
                emotion.getId(),
                emotion.getName(),
                false
        );
    }
}

