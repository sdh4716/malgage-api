package com.darong.malgage_api.controller.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InstallmentDetailDto {
    private Long recordId;
    private String description;
    private int totalAmount;
    private int monthlyAmount;
    private int currentMonth;
    private int totalMonths;
    private String progress;           // "3/12"
    private LocalDateTime nextPaymentDate;
}