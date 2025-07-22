package com.darong.malgage_api.global.gpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// RecordAnalysisResponse DTO 클래스
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordAnalysisResponse {
    private boolean success;
    private String type;              // "income" or "expense"
    private Long amount;              // 금액
    private String description;       // 설명
    private String category;          // 카테고리
    private String paymentMethod;     // 결제수단
    private String emotion;           // 감정
    @JsonProperty("isInstallment")
    private boolean isInstallment;    // 할부 여부
    private int installmentMonths;    // 할부 개월수
    private String errorMessage;      // 오류 메시지
}
