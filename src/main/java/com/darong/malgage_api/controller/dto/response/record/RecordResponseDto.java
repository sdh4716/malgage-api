package com.darong.malgage_api.controller.dto.response.record;

import com.darong.malgage_api.domain.record.InstallmentSchedule;
import com.darong.malgage_api.domain.record.Record;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @JsonProperty("isInstallment")
    private boolean isInstallment;

    private int installmentMonths;

    private String memo;

    // ➕ 할부 관련 필드
    private Integer monthlyAmount;  // 이번 달 납부 금액
    private String installmentProgress;        // 예: "3/12"

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
                .installmentMonths(record.getInstallmentMonths())
                .memo(record.getMemo())
                .build();
    }


    /**
     * 할부 회차를 위한 팩토리 메서드
     */
    public static RecordResponseDto fromInstallment(InstallmentSchedule schedule) {
        Record record = schedule.getRecord();

        return RecordResponseDto.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .categoryId(record.getCategory().getId())
                .categoryName(record.getCategory().getName())
                .categoryIcon(record.getCategory().getIconName())
                .emotionId(record.getEmotion().getId())
                .emotionName(record.getEmotion().getName())
                .emotionIcon(record.getEmotion().getIconName())
                .date(schedule.getScheduledDate()) // ❗️중요: 회차 납부일
                .type(record.getType().toString())
                .paymentMethod(record.getPaymentMethod().toString())
                .isInstallment(true)
                .installmentMonths(record.getInstallmentMonths())
                .memo(record.getMemo())
                .monthlyAmount(schedule.getMonthlyAmount())
                .installmentProgress(schedule.getInstallmentIndex() + "/" + record.getInstallmentMonths())
                .build();
    }
}
