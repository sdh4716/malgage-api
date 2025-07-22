package com.darong.malgage_api.global.gpt.service;

import com.darong.malgage_api.global.gpt.config.OpenAiConfig;
import com.darong.malgage_api.global.gpt.dto.OpenAiRequest;
import com.darong.malgage_api.global.gpt.dto.OpenAiResponse;
import com.darong.malgage_api.global.gpt.exception.OpenAiApiException;
import com.darong.malgage_api.global.gpt.exception.RateLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * OpenAI API를 호출하기 위한 클라이언트 클래스
 * - API 호출 실패 시 자동으로 재시도
 * - Rate Limit (요청 한도 초과) 상황 처리
 * - 지수적 백오프 (exponential backoff) 적용
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiApiClient {

    private final WebClient.Builder webClientBuilder; // HTTP 클라이언트 빌더
    private final OpenAiConfig config; // OpenAI API 설정 정보 (API 키, URL 등)

    /**
     * OpenAI API를 호출하는 메인 메서드
     * @param request API 요청 데이터
     * @return API 응답 데이터
     */
    public OpenAiResponse callApi(OpenAiRequest request) {
        WebClient webClient = createWebClient(); // HTTP 클라이언트 생성
        return callWithRetry(webClient, request); // 재시도 로직과 함께 API 호출
    }

    /**
     * OpenAI API 호출을 위한 WebClient 생성
     * - 기본 URL과 인증 헤더를 설정
     */
    private WebClient createWebClient() {
        return webClientBuilder
                .baseUrl(config.getBaseUrl()) // OpenAI API 기본 URL 설정
                .defaultHeader("Authorization", "Bearer " + config.getApiKey()) // API 키를 Bearer 토큰으로 설정
                .defaultHeader("Content-Type", "application/json") // JSON 형식으로 요청
                .build();
    }

    /**
     * 재시도 로직이 포함된 API 호출 메서드
     * - 실패 시 설정된 횟수만큼 재시도
     * - 429 에러(Rate Limit) 발생 시 특별 처리
     */
    private OpenAiResponse callWithRetry(WebClient webClient, OpenAiRequest request) {
        // 설정된 최대 재시도 횟수만큼 반복
        for (int attempt = 1; attempt <= config.getMaxRetries(); attempt++) {
            try {
                log.info("API 호출 시도 {}/{}", attempt, config.getMaxRetries());

                return webClient.post() // POST 요청
                        .bodyValue(request) // 요청 데이터를 JSON으로 전송
                        .retrieve() // 응답 받기
                        .onStatus(status -> status.value() == 429, this::handleRateLimit) // 429 에러 시 특별 처리
                        .bodyToMono(OpenAiResponse.class) // 응답을 OpenAiResponse 객체로 변환
                        .block(); // 동기적으로 결과 대기 (비동기 -> 동기 변환)

            } catch (Exception e) {
                // 재시도 가능한 에러인지 확인
                if (shouldRetry(e, attempt)) {
                    waitBeforeRetry(attempt); // 대기 후 재시도
                } else {
                    // 재시도 불가능한 에러면 예외 던지기
                    throw new OpenAiApiException("API 호출 실패", e);
                }
            }
        }

        // 모든 재시도가 실패한 경우
        throw new OpenAiApiException("모든 재시도 실패");
    }

    /**
     * 429 Too Many Requests (Rate Limit) 에러 처리
     * - 로그를 남기고 RateLimitException 예외 발생
     */
    private Mono<? extends Throwable> handleRateLimit(ClientResponse response) {
        log.warn("429 Too Many Requests 발생");
        return response.bodyToMono(String.class) // 응답 본문을 문자열로 읽기
                .doOnNext(body -> log.warn("Rate limit 응답: {}", body)) // 응답 내용 로깅
                .then(Mono.error(new RateLimitException("Rate limit exceeded"))); // 예외 발생
    }

    /**
     * 재시도 가능한 예외인지 판단
     * - 최대 재시도 횟수를 넘지 않았고
     * - 429 에러이거나 RateLimitException인 경우에만 재시도
     */
    private boolean shouldRetry(Exception e, int attempt) {
        return attempt < config.getMaxRetries() &&
                (e.getMessage().contains("429") || e instanceof RateLimitException);
    }

    /**
     * 재시도 전 대기 (지수적 백오프)
     * - 재시도 횟수가 늘어날수록 대기 시간이 기하급수적으로 증가
     * - 1회차: 기본 대기시간, 2회차: 2배, 3회차: 4배, 4회차: 8배...
     */
    private void waitBeforeRetry(int attempt) {
        // 2의 attempt 제곱 * 기본 대기시간으로 계산
        long waitTime = (long) Math.pow(2, attempt) * config.getBaseRetryDelayMs();
        log.info("{}ms 대기 후 재시도", waitTime);

        try {
            Thread.sleep(waitTime); // 계산된 시간만큼 대기
        } catch (InterruptedException ie) {
            // 대기 중 인터럽트가 발생한 경우
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
            throw new OpenAiApiException("재시도 중 인터럽트 발생", ie);
        }
    }
}