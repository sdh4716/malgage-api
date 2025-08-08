package com.darong.malgage_api.controller.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BudgetProgressDto {
    private int budgetAmount;
    private int usedAmount;
    private int remainingAmount;
    private double usageRate; // %
    private int dailyBudget;  // 남은 일수 기준 1일 가능액
}
