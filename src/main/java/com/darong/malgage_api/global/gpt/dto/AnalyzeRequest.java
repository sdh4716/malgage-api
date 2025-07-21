package com.darong.malgage_api.global.gpt.dto;

// 요청 DTO 클래스
public class AnalyzeRequest {
    private String text;

    public AnalyzeRequest() {
    }

    public AnalyzeRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
