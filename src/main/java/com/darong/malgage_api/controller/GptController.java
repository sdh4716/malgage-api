package com.darong.malgage_api.controller;

import com.darong.malgage_api.controller.dto.request.AnalysisRequest;
import com.darong.malgage_api.controller.dto.response.MultipleRecordAnalysisResponse;
import com.darong.malgage_api.external.gpt.service.GptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GptController {

    private final GptService gptService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("=== 테스트 엔드포인트 호출됨 ===");
        return ResponseEntity.ok("컨트롤러 동작 중");
    }

    @PostMapping("/records/analyze")
    public ResponseEntity<MultipleRecordAnalysisResponse> analyzeText(@RequestBody AnalysisRequest request) {
        try {
            log.info("기록 분석 요청: {}", request.getText());

            MultipleRecordAnalysisResponse response = gptService.extractRecordInfo(request.getText());

            log.info("분석 결과: 성공={}, 기록수={}", response.isSuccess(), response.getRecordCount());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("분석 중 오류 발생: {}", e.getMessage(), e);

            // 오류 응답 생성
            MultipleRecordAnalysisResponse errorResponse = MultipleRecordAnalysisResponse.builder()
                    .success(false)
                    .recordCount(0)
                    .records(null)
                    .errorMessage("서버 오류가 발생했습니다. 다시 시도해주세요.")
                    .build();

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}