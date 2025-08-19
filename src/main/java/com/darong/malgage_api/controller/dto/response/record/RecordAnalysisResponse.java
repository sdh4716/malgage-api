package com.darong.malgage_api.controller.dto.response.record;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GPT 분석 결과의 단일 거래 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordAnalysisResponse {

    private String type;               // "income" or "expense"
    private Long amount;               // 금액 (null 가능)
    private String memo;               // 설명

    private Long categoryId;           // 카테고리 ID (null 가능)
    private String paymentMethod;      // 결제수단 (PaymentMethod Enum 값)

    private Long emotionId;            // 감정 ID (null 가능)

    @JsonProperty("isInstallment")
    private Boolean isInstallment;     // 할부 여부 (null 가능)

    private Integer installmentMonths; // 할부 개월수 (null 가능)
}
