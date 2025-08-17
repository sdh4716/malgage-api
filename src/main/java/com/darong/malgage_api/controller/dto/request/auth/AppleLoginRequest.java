package com.darong.malgage_api.controller.dto.request.auth;


import lombok.Getter;

@Getter
public class AppleLoginRequest {
    private String idToken;  // iOS/Flutter에서 받은 Apple ID Token (JWT)
}