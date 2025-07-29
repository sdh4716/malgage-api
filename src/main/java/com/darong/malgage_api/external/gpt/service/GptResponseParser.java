package com.darong.malgage_api.external.gpt.service;

import com.darong.malgage_api.controller.dto.response.RecordAnalysisResponse;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class GptResponseParser {

    private final ObjectMapper objectMapper;

    public List<RecordAnalysisResponse> parseToRecords(String gptResponse) {
        try {
            String jsonOnly = extractJsonFromResponse(gptResponse);
            validateJson(jsonOnly);

            JsonNode rootNode = objectMapper.readTree(jsonOnly);
            log.info("JSON 파싱 성공, 노드 타입: {}", rootNode.getNodeType());

            return parseJsonNode(rootNode);

        } catch (Exception e) {
            log.error("GPT 응답 파싱 중 오류 발생", e);
            throw new GptResponseParsingException("응답 파싱 실패: " + gptResponse, e);
        }
    }

    private String extractJsonFromResponse(String response) {
        JsonExtractor extractor = new JsonExtractor(response);
        return extractor.extract();
    }

    private void validateJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new GptResponseParsingException("추출된 JSON이 비어있음");
        }
    }

    private List<RecordAnalysisResponse> parseJsonNode(JsonNode rootNode) throws JsonProcessingException {
        List<RecordAnalysisResponse> records = new ArrayList<>();

        if (rootNode.isArray()) {
            log.info("배열 형태 응답, 배열 크기: {}", rootNode.size());
            records.addAll(parseArrayNode(rootNode));
        } else if (rootNode.isObject()) {
            log.info("단일 객체 형태 응답");
            records.add(parseObjectNode(rootNode));
        } else {
            throw new GptResponseParsingException("예상하지 못한 JSON 형태: " + rootNode.getNodeType());
        }

        log.info("파싱 완료, 총 {} 개의 레코드 생성", records.size());
        return records;
    }

    private List<RecordAnalysisResponse> parseArrayNode(JsonNode arrayNode) throws JsonProcessingException {
        List<RecordAnalysisResponse> records = new ArrayList<>();

        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode node = arrayNode.get(i);
            log.info("배열 요소 [{}]: {}", i, node.toString());

            RecordAnalysisResponse record = parseObjectNode(node);
            records.add(record);

            log.info("파싱된 레코드 [{}]: type={}, amount={}, isInstallment={}",
                    i, record.getType(), record.getAmount(), record.isInstallment());
        }

        return records;
    }

    private RecordAnalysisResponse parseObjectNode(JsonNode node) throws JsonProcessingException {
        String nodeJson = objectMapper.writeValueAsString(node);
        log.info("노드 JSON 문자열: {}", nodeJson);

        return objectMapper.readValue(nodeJson, RecordAnalysisResponse.class);
    }
}
