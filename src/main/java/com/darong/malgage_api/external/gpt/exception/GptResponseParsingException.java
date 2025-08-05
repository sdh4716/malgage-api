package com.darong.malgage_api.external.gpt.exception;

public class GptResponseParsingException extends RuntimeException {
    public GptResponseParsingException(String message) {
        super(message);
    }

    public GptResponseParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
