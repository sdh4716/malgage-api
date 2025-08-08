package com.darong.malgage_api.controller.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InsightDto {
    private String type;        // warning/tip/info
    private String title;
    private String description;
    private String suggestion;
    private String emoji;
    private int priority;
}