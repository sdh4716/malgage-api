package com.darong.malgage_api.external.gpt.service;

import com.darong.malgage_api.controller.dto.response.record.RecordAnalysisResponse;
import com.darong.malgage_api.external.gpt.exception.GptResponseParsingException;
import com.darong.malgage_api.external.gpt.util.JsonExtractor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class GptResponseParser {

    private final ObjectMapper objectMapper;

    public List<RecordAnalysisResponse> parseToRecords(String gptResponse) {
        try {
            String jsonOnly = new JsonExtractor(gptResponse).extract();
            validateJson(jsonOnly);

            JsonNode rootNode = objectMapper.readTree(jsonOnly);
            return parseJsonNode(rootNode);

        } catch (Exception e) {
            log.error("GPT 응답 파싱 중 오류 발생", e);
            throw new GptResponseParsingException("응답 파싱 실패: " + gptResponse, e);
        }
    }

    private void validateJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new GptResponseParsingException("추출된 JSON이 비어있음");
        }
    }

    private List<RecordAnalysisResponse> parseJsonNode(JsonNode rootNode) throws JsonProcessingException {
        if (rootNode.isArray()) {
            return parseArrayNode(rootNode);
        } else if (rootNode.isObject()) {
            return List.of(parseObjectNode(rootNode));
        }
        throw new GptResponseParsingException("예상하지 못한 JSON 형태: " + rootNode.getNodeType());
    }

    private List<RecordAnalysisResponse> parseArrayNode(JsonNode arrayNode) throws JsonProcessingException {
        List<RecordAnalysisResponse> records = new ArrayList<>();
        for (JsonNode node : arrayNode) {
            records.add(parseObjectNode(node));
        }
        return records;
    }

    private RecordAnalysisResponse parseObjectNode(JsonNode node) throws JsonProcessingException {
        return objectMapper.readValue(objectMapper.writeValueAsString(node), RecordAnalysisResponse.class);
    }

    private void setNullIfMissing(JsonNode node, String field, BiConsumer<Long, Void> setter) {
        if (!node.has(field) || node.get(field).isNull()) {
            log.debug("{} 없음 → null 처리", field);
            setter.accept(null, null);
        }
    }
}
