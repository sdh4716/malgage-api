package com.darong.malgage_api.external.gpt.service;

import com.darong.malgage_api.controller.dto.response.record.MultipleRecordAnalysisResponse;
import com.darong.malgage_api.domain.user.User;
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
    private final PromptTemplateService promptTemplateService;

    public MultipleRecordAnalysisResponse extractRecordInfo(User user, String userText) {
        try {
            OpenAiResponse response = callApi(user, userText);
            List<RecordAnalysisResponse> records = responseParser.parseToRecords(extractContent(response));
            return createSuccessResponse(records);

        } catch (RateLimitException e) {
            log.error("Rate limit 초과", e);
            return ResponseFactory.createRateLimitResponse();

        } catch (Exception e) {
            log.error("GPT 분석 중 오류 발생", e);
            return ResponseFactory.createDefaultResponse();
        }
    }

    private OpenAiResponse callApi(User user, String userText) {
        String systemPrompt = promptTemplateService.buildSystemPrompt(user);
        OpenAiRequest request = new OpenAiRequest(
                config.getModel(),
                List.of(
                        new OpenAiMessage("system", systemPrompt),
                        new OpenAiMessage("user", userText)
                ),
                config.getTemperature()
        );
        return apiClient.callApi(request);
    }

    private String extractContent(OpenAiResponse response) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new OpenAiApiException("OpenAI 응답이 null이거나 빈 응답");
        }
        String content = response.getChoices().get(0).getMessage().getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new OpenAiApiException("GPT 응답 내용이 비어있음");
        }
        return content.trim();
    }

    private MultipleRecordAnalysisResponse createSuccessResponse(List<RecordAnalysisResponse> records) {
        return MultipleRecordAnalysisResponse.builder()
                .success(true)
                .recordCount(records.size())
                .records(records)
                .build();
    }
}