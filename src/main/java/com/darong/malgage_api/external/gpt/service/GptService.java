package com.darong.malgage_api.external.gpt.service;

import com.darong.malgage_api.controller.dto.response.record.MultipleRecordAnalysisResponse;
import com.darong.malgage_api.external.gpt.dto.OpenAiMessage;
import com.darong.malgage_api.external.gpt.dto.OpenAiRequest;
import com.darong.malgage_api.external.gpt.dto.OpenAiResponse;
import com.darong.malgage_api.controller.dto.response.record.RecordAnalysisResponse;
import com.darong.malgage_api.global.config.OpenAiConfig;
import com.darong.malgage_api.global.constants.PromptConstants;
import com.darong.malgage_api.external.gpt.exception.OpenAiApiException;
import com.darong.malgage_api.external.gpt.exception.RateLimitException;
import com.darong.malgage_api.external.gpt.factory.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptService {

    private final OpenAiApiClient apiClient;
    private final GptResponseParser responseParser;
    private final OpenAiConfig config;

    public MultipleRecordAnalysisResponse extractRecordInfo(String userText) {
        log.info("=== GPT 분석 시작 ===");
        log.info("입력 텍스트: {}", userText);

        try {
            OpenAiRequest request = createRequest(userText);
            OpenAiResponse response = apiClient.callApi(request);

            String gptResponse = extractContent(response);
            List<RecordAnalysisResponse> records = responseParser.parseToRecords(gptResponse);

            return createSuccessResponse(records);

        } catch (RateLimitException e) {
            log.error("Rate limit 초과", e);
            return ResponseFactory.createRateLimitResponse();
        } catch (Exception e) {
            log.error("GPT 분석 중 오류 발생", e);
            return ResponseFactory.createDefaultResponse();
        }
    }

    private OpenAiRequest createRequest(String userText) {
        OpenAiMessage system = new OpenAiMessage("system", PromptConstants.SYSTEM_PROMPT);
        OpenAiMessage user = new OpenAiMessage("user", userText);

        return new OpenAiRequest(
                config.getModel(),
                List.of(system, user),
                config.getTemperature()
        );
    }

    private String extractContent(OpenAiResponse response) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new OpenAiApiException("OpenAI 응답이 null이거나 빈 응답");
        }

        String content = response.getChoices().get(0).getMessage().getContent();
        log.info("GPT 원본 응답: {}", content);

        if (content == null || content.trim().isEmpty()) {
            throw new OpenAiApiException("GPT 응답 내용이 비어있음");
        }

        return content.trim();
    }

    private MultipleRecordAnalysisResponse createSuccessResponse(List<RecordAnalysisResponse> records) {
        MultipleRecordAnalysisResponse result = MultipleRecordAnalysisResponse.builder()
                .success(true)
                .recordCount(records.size())
                .records(records)
                .build();

        log.info("최종 응답 생성: success={}, recordCount={}", result.isSuccess(), result.getRecordCount());
        return result;
    }
}