package com.darong.malgage_api.controller.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmotionalSpendingDto {
    private Long emotionId;
    private String emotionName;
    private String emotionIcon;
    private int amount;       // 할부 월금액 반영된 총액
    private double percentage;
}