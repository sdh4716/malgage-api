package com.darong.malgage_api.global.gpt.factory;

import com.darong.malgage_api.global.gpt.dto.MultipleRecordAnalysisResponse;
import com.darong.malgage_api.global.gpt.dto.RecordAnalysisResponse;

import java.util.List;

public class ResponseFactory {

    public static MultipleRecordAnalysisResponse createDefaultResponse() {
        RecordAnalysisResponse defaultRecord = createErrorRecord(
                "분석 실패",
                "텍스트 분석에 실패했습니다."
        );

        return MultipleRecordAnalysisResponse.builder()
                .success(false)
                .recordCount(0)
                .records(List.of(defaultRecord))
                .errorMessage("텍스트 분석에 실패했습니다. 다시 시도해주세요.")
                .build();
    }

    public static MultipleRecordAnalysisResponse createRateLimitResponse() {
        RecordAnalysisResponse defaultRecord = createErrorRecord(
                "요청 한도 초과",
                "API 요청 한도에 도달했습니다."
        );

        return MultipleRecordAnalysisResponse.builder()
                .success(false)
                .recordCount(0)
                .records(List.of(defaultRecord))
                .errorMessage("API 요청 한도에 도달했습니다. 잠시 후 다시 시도해주세요.")
                .build();
    }

    private static RecordAnalysisResponse createErrorRecord(String description, String errorMessage) {
        return RecordAnalysisResponse.builder()
                .success(false)
                .type("expense")
                .amount(0L)
                .description(description)
                .category("기타")
                .paymentMethod("신용카드")
                .emotion("보통")
                .isInstallment(false)
                .installmentMonths(0)
                .errorMessage(errorMessage)
                .build();
    }

    private ResponseFactory() {} // 유틸리티 클래스
}
