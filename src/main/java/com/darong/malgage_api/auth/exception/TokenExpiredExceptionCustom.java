package com.darong.malgage_api.auth.exception;

public class TokenExpiredExceptionCustom extends RuntimeException {
    public TokenExpiredExceptionCustom(String message) {
        super(message);
    }
}
