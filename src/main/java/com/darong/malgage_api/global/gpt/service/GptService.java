package com.darong.malgage_api.global.gpt;

import com.darong.malgage_api.global.gpt.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    public MultipleRecordAnalysisResponse extractRecordInfo(String userText) {
        log.info("=== GPT 분석 시작 ===");
        log.info("입력 텍스트: {}", userText);

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl("https://api.openai.com/v1/chat/completions")
                    .defaultHeader("Authorization", "Bearer " + apiKey)
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            String systemPrompt = """
                한국어 가계부 입력을 JSON 배열로 변환하세요.
                
                필수 필드:
                - type: "income"/"expense"
                - amount: 숫자 (총액)
                - description: 간단설명
                - category: 식비,카페,교통/차량,쇼핑,의료,문화생활,마트/쇼핑몰,교육,투자,미용,운동,술/유흥,캠핑,경조사/선물,의류,기타,급여,용돈,부업,투자수익,기타수입
                - paymentMethod: 신용카드,체크카드,현금,계좌이체
                - emotion: 기쁨,만족,보통,아쉬움,후회,화남
                - isInstallment: true/false
                - installmentMonths: 숫자 (할부 개월수, 일시불이면 0)
                
                할부 판단 규칙:
                - "할부", "분할", "개월", "12개월", "24개월" 등이 언급되면 isInstallment: true
                - "일시불", "한번에", "현금" 등이 언급되면 isInstallment: false
                - 할부 개월수가 명시되면 installmentMonths에 해당 숫자 입력
                - 할부라고 하지만 개월수가 불명확하면 installmentMonths: 12 (기본값)
                - 일시불이거나 할부가 아니면 installmentMonths: 0
                
                반드시 JSON 배열 형태로만 응답하세요.
                """;

            OpenAiMessage system = new OpenAiMessage("system", systemPrompt);
            OpenAiMessage user = new OpenAiMessage("user", userText);

            OpenAiRequest request = new OpenAiRequest(
                    model,
                    List.of(system, user),
                    0.3
            );

            log.info("OpenAI API 호출 시작");

            OpenAiResponse response = callWithRetry(webClient, request);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                log.error("OpenAI 응답이 null이거나 빈 응답");
                return createDefaultMultipleResponse();
            }

            String gptResponse = response.getChoices().get(0).getMessage().getContent();
            log.info("GPT 원본 응답: {}", gptResponse);

            if (gptResponse == null || gptResponse.trim().isEmpty()) {
                log.error("GPT 응답 내용이 비어있음");
                return createDefaultMultipleResponse();
            }

            return parseMultipleGptResponse(gptResponse.trim());

        } catch (Exception e) {
            log.error("GPT 분석 중 오류 발생", e);

            if (e.getMessage().contains("429")) {
                return createRateLimitResponse();
            }

            return createDefaultMultipleResponse();
        }
    }

    private MultipleRecordAnalysisResponse parseMultipleGptResponse(String gptResponse) {
        log.info("=== GPT 응답 파싱 시작 ===");
        log.info("파싱할 응답: {}", gptResponse);

        try {
            String jsonOnly = extractJsonFromResponse(gptResponse);
            log.info("추출된 JSON: {}", jsonOnly);

            if (jsonOnly == null || jsonOnly.trim().isEmpty()) {
                log.error("추출된 JSON이 비어있음");
                return createDefaultMultipleResponse();
            }

            JsonNode rootNode = objectMapper.readTree(jsonOnly);
            log.info("JSON 파싱 성공, 노드 타입: {}", rootNode.getNodeType());

            List<RecordAnalysisResponse> records = new ArrayList<>();

            if (rootNode.isArray()) {
                log.info("배열 형태 응답, 배열 크기: {}", rootNode.size());
                for (int i = 0; i < rootNode.size(); i++) {
                    JsonNode node = rootNode.get(i);
                    log.info("배열 요소 [{}]: {}", i, node.toString());

                    // 중요: JsonNode를 직접 DTO로 변환하지 말고 다시 JSON 문자열로 변환 후 파싱
                    String nodeJson = objectMapper.writeValueAsString(node);
                    log.info("노드 JSON 문자열: {}", nodeJson);

                    RecordAnalysisResponse record = objectMapper.readValue(nodeJson, RecordAnalysisResponse.class);
                    records.add(record);

                    log.info("파싱된 레코드 [{}]: type={}, amount={}, description={}, isInstallment={}, installmentMonths={}",
                            i, record.getType(), record.getAmount(), record.getDescription(),
                            record.isInstallment(), record.getInstallmentMonths());
                }
            } else if (rootNode.isObject()) {
                log.info("단일 객체 형태 응답");
                String nodeJson = objectMapper.writeValueAsString(rootNode);
                RecordAnalysisResponse record = objectMapper.readValue(nodeJson, RecordAnalysisResponse.class);
                records.add(record);

                log.info("파싱된 단일 레코드: type={}, amount={}, description={}, isInstallment={}, installmentMonths={}",
                        record.getType(), record.getAmount(), record.getDescription(),
                        record.isInstallment(), record.getInstallmentMonths());
            } else {
                log.error("예상하지 못한 JSON 형태: {}", rootNode.getNodeType());
                return createDefaultMultipleResponse();
            }

            log.info("파싱 완료, 총 {} 개의 레코드 생성", records.size());

            MultipleRecordAnalysisResponse result = MultipleRecordAnalysisResponse.builder()
                    .success(true)
                    .recordCount(records.size())
                    .records(records)
                    .build();

            log.info("최종 응답 생성: success={}, recordCount={}", result.isSuccess(), result.getRecordCount());
            return result;

        } catch (Exception e) {
            log.error("GPT 응답 파싱 중 오류 발생", e);
            log.error("파싱 실패한 원본 응답: {}", gptResponse);
            return createDefaultMultipleResponse();
        }
    }

    private String extractJsonFromResponse(String response) {
        int arrayStart = response.indexOf("[");
        int arrayEnd = response.lastIndexOf("]");
        int objectStart = response.indexOf("{");
        int objectEnd = response.lastIndexOf("}");

        String extracted = null;

        if (arrayStart != -1 && arrayEnd != -1 && arrayEnd > arrayStart) {
            if (objectStart == -1 || arrayStart < objectStart) {
                extracted = response.substring(arrayStart, arrayEnd + 1);
                log.info("배열 JSON 추출: {}", extracted);
            }
        }

        if (extracted == null && objectStart != -1 && objectEnd != -1 && objectEnd > objectStart) {
            extracted = response.substring(objectStart, objectEnd + 1);
            log.info("객체 JSON 추출: {}", extracted);
        }

        if (extracted == null) {
            log.warn("JSON 구조를 찾을 수 없음, 원본 그대로 반환");
            extracted = response.trim();
        }

        return extracted;
    }

    private OpenAiResponse callWithRetry(WebClient webClient, OpenAiRequest request) {
        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("API 호출 시도 {}/{}", attempt, maxRetries);

                return webClient.post()
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(
                                status -> status.value() == 429,
                                clientResponse -> {
                                    log.warn("429 Too Many Requests 발생");
                                    return Mono.error(new RuntimeException("Rate limit exceeded"));
                                }
                        )
                        .bodyToMono(OpenAiResponse.class)
                        .block();

            } catch (Exception e) {
                log.warn("API 호출 실패 (시도 {}): {}", attempt, e.getMessage());

                if (attempt < maxRetries && e.getMessage().contains("429")) {
                    long waitTime = (long) Math.pow(2, attempt) * 1000;
                    log.info("{}ms 대기 후 재시도", waitTime);

                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("재시도 중 인터럽트", ie);
                    }
                } else {
                    throw e;
                }
            }
        }

        throw new RuntimeException("모든 재시도 실패");
    }

    private MultipleRecordAnalysisResponse createDefaultMultipleResponse() {
        RecordAnalysisResponse defaultRecord = RecordAnalysisResponse.builder()
                .success(false)
                .type("expense")
                .amount(0L)
                .description("분석 실패")
                .category("기타")
                .paymentMethod("신용카드")
                .emotion("보통")
                .isInstallment(false)
                .installmentMonths(0)
                .errorMessage("텍스트 분석에 실패했습니다.")
                .build();

        return MultipleRecordAnalysisResponse.builder()
                .success(false)
                .recordCount(0)
                .records(List.of(defaultRecord))
                .errorMessage("텍스트 분석에 실패했습니다. 다시 시도해주세요.")
                .build();
    }

    private MultipleRecordAnalysisResponse createRateLimitResponse() {
        RecordAnalysisResponse defaultRecord = RecordAnalysisResponse.builder()
                .success(false)
                .type("expense")
                .amount(0L)
                .description("요청 한도 초과")
                .category("기타")
                .paymentMethod("신용카드")
                .emotion("보통")
                .isInstallment(false)
                .installmentMonths(0)
                .errorMessage("API 요청 한도에 도달했습니다.")
                .build();

        return MultipleRecordAnalysisResponse.builder()
                .success(false)
                .recordCount(0)
                .records(List.of(defaultRecord))
                .errorMessage("API 요청 한도에 도달했습니다. 잠시 후 다시 시도해주세요.")
                .build();
    }
}