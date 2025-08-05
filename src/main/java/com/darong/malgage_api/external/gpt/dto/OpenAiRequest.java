package com.darong.malgage_api.external.gpt.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class OpenAiRequest {
    private String model;
    private List<OpenAiMessage> messages;
    private double temperature;
}
