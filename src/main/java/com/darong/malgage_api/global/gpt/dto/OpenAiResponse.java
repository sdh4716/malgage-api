package com.darong.malgage_api.global.gpt.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenAiResponse {
    private List<OpenAiChoice> choices;
}
