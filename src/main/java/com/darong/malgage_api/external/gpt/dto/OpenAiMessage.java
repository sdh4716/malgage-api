package com.darong.malgage_api.external.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenAiMessage {
    private String role;  // user or system
    private String content;
}
