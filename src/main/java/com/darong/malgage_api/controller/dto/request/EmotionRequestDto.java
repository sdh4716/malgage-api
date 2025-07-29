// domain/emotion/dto/EmotionRequestDto.java
package com.darong.malgage_api.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionRequestDto {

    @NotBlank(message = "감정명은 필수입니다.")
    @Size(max = 50, message = "감정명은 50자를 초과할 수 없습니다.")
    private String name;

    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다.")
    private Integer sortOrder;
}