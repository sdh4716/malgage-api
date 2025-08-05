package com.darong.malgage_api.external.gpt.exception;

public class RateLimitException extends OpenAiApiException {
    public RateLimitException(String message) {
        super(message);
    }
}
