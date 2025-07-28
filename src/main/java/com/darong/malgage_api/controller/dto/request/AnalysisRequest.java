package com.darong.malgage_api.global.gpt.dto;

import lombok.Getter;
import lombok.Setter;

// 요청 DTO 클래스
@Setter
@Getter
public class AnalysisRequest {
    private String text;

    public AnalysisRequest() {
    }

    public AnalysisRequest(String text) {
        this.text = text;
    }

}
