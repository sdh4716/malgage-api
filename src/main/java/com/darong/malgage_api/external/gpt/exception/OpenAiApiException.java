package com.darong.malgage_api.external.gpt.exception;

public class OpenAiApiException extends RuntimeException {
    public OpenAiApiException(String message) {
        super(message);
    }

    public OpenAiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
