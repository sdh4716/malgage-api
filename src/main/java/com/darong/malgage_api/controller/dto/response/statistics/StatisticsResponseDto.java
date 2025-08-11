package com.darong.malgage_api.controller.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StatisticsResponseDto {
    private PeriodOverviewDto overview;
    private BudgetProgressDto budget;
    private List<EmotionalSpendingDto> emotionalSpending;
    private List<CategorySpendingDto> categorySpending;
    private List<PaymentMethodSpendingDto> paymentMethods;
    private InstallmentSummaryDto installments;
    private List<InsightDto> insights;
}