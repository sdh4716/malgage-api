package com.darong.malgage_api.global.gpt.exception;

public class GptResponseParsingException extends RuntimeException {
    public GptResponseParsingException(String message) {
        super(message);
    }

    public GptResponseParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
