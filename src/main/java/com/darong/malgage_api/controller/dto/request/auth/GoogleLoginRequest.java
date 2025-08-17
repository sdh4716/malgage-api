package com.darong.malgage_api.controller.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequest {
    private String idToken;
}
