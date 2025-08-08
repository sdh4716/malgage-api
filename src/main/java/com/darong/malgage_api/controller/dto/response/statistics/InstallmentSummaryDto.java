package com.darong.malgage_api.controller.dto.response.statistics;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InstallmentSummaryDto {
    private int activeCount;       // 이번달 납부 중인 레코드 수 (distinct record)
    private int monthlyPayment;    // 이번달 총 할부금
    private double paymentRatio;   // 소득 대비 비율(%)
    private List<InstallmentDetailDto> details;
}