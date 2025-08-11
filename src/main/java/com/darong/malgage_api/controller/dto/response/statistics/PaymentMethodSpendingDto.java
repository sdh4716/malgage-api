package com.darong.malgage_api.controller.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentMethodSpendingDto {
    private String paymentMethod;      // enum name
    private String paymentMethodName;  // 한글명 (매핑)
    private int amount;                // 할부 월금액 반영된 총액
    private double percentage;
    private int transactionCount;
}