package com.darong.malgage_api.global.gpt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openai")
@Data
public class OpenAiConfig {
    private String apiKey;
    private String model;
    private String baseUrl = "https://api.openai.com/v1/chat/completions";
    private int maxRetries = 3;
    private long baseRetryDelayMs = 1000L;
    private double temperature = 0.3;
}