package com.darong.malgage_api.external.gpt.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenAiResponse {
    private List<OpenAiChoice> choices;
}
