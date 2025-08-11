package com.darong.malgage_api.controller.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategorySpendingDto {
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private int amount;           // 할부 월금액 반영된 총액
    private double percentage;
    private int transactionCount; // 일반건수 + 할부회차건수
}