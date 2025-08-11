package com.darong.malgage_api.controller.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PeriodOverviewDto {
    private int totalIncome;
    private int totalExpense;
    private int lastPeriodExpense;
    private int netIncome;
    private double changePercent; // (이번달 지출 - 전월 지출)/전월 지출 * 100
}