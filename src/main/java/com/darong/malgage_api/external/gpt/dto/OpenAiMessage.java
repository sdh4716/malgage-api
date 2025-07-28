package com.darong.malgage_api.global.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenAiMessage {
    private String role;  // user or system
    private String content;
}
