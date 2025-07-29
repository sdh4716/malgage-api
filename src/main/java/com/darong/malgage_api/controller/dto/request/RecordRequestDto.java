package com.darong.malgage_api.controller.dto.request;

import com.darong.malgage_api.domain.record.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RecordRequestDto {
    private int amount;
    private RecordType type;
    private LocalDateTime date;
    private Long categoryId;
    private Long emotionId;
    private PaymentMethod paymentMethod;
    private boolean isInstallment;
    private int installmentMonth;
    private String memo;

}