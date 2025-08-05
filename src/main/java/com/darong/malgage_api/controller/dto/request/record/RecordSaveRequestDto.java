package com.darong.malgage_api.controller.dto.request.record;

import com.darong.malgage_api.domain.record.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RecordSaveRequestDto {
    private int amount;
    private RecordType type;
    private LocalDateTime date;
    private Long categoryId;
    private Long emotionId;
    private PaymentMethod paymentMethod;
    @JsonProperty("isInstallment")
    private boolean isInstallment;
    private int installmentMonths;
    private String memo;

}