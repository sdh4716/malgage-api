package com.darong.malgage_api.controller.dto.response;

import com.darong.malgage_api.domain.record.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class RecordResponseDto {
    private Long id;
    private int amount;
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private Long emotionId;
    private String emotionName;
    private String emotionIcon;
    private LocalDateTime date;
    private String type;
    private String paymentMethod;
    private boolean isInstallment;
    private int installmentMonth;
    private String memo;

    public static RecordResponseDto from(Record record) {
        return RecordResponseDto.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .categoryId(record.getCategory().getId())
                .categoryName(record.getCategory().getName())
                .categoryIcon(record.getCategory().getIconName())
                .emotionId(record.getEmotion().getId())
                .emotionName(record.getEmotion().getName())
                .emotionIcon(record.getEmotion().getIconName())
                .date(record.getDate())
                .type(record.getType().toString())
                .paymentMethod(record.getPaymentMethod().toString())
                .isInstallment(record.getIsInstallment())
                .installmentMonth(record.getInstallmentMonths())
                .memo(record.getMemo())
                .build();
    }
}