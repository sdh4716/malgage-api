package com.darong.malgage_api.global.gpt.dto;

import com.darong.malgage_api.global.gpt.dto.RecordAnalysisResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 다중 기록 응답 DTO 추가
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultipleRecordAnalysisResponse {
    private boolean success;
    private int recordCount;                    // 추출된 기록 개수
    private List<RecordAnalysisResponse> records; // 기록 리스트
    private String errorMessage;                // 오류 메시지
}
