package com.darong.malgage_api.global.gpt.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonExtractor {

    private final String response;

    public JsonExtractor(String response) {
        this.response = response;
    }

    public String extract() {
        String extracted = extractArray();
        if (extracted == null) {
            extracted = extractObject();
        }
        if (extracted == null) {
            log.warn("JSON 구조를 찾을 수 없음, 원본 그대로 반환");
            extracted = response.trim();
        }

        return extracted;
    }

    private String extractArray() {
        int start = response.indexOf("[");
        int end = response.lastIndexOf("]");

        if (start != -1 && end != -1 && end > start) {
            int objectStart = response.indexOf("{");
            if (objectStart == -1 || start < objectStart) {
                String result = response.substring(start, end + 1);
                log.info("배열 JSON 추출: {}", result);
                return result;
            }
        }

        return null;
    }

    private String extractObject() {
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start != -1 && end != -1 && end > start) {
            String result = response.substring(start, end + 1);
            log.info("객체 JSON 추출: {}", result);
            return result;
        }

        return null;
    }
}
