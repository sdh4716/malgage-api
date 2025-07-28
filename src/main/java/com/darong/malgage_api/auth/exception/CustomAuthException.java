package com.darong.malgage_api.auth.exception;

import org.springframework.http.HttpStatus;

public class CustomAuthException extends RuntimeException {

    private final HttpStatus status;

    public CustomAuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomAuthException(String message) {
        this(message, HttpStatus.UNAUTHORIZED);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
