package com.darong.malgage_api.domain.record.dto;

import com.darong.malgage_api.domain.record.*;
import com.darong.malgage_api.domain.record.Record;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RecordRequestDto {
    private int amount;
    private RecordType type;
    private LocalDate date;
    private Long categoryId;
    private Long emotionId;
    private PaymentMethod paymentMethod;
    private boolean isInstallment;
    private int installmentMonth;
    private String memo;

}