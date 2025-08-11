package com.darong.malgage_api.external.gpt.service;

import com.darong.malgage_api.controller.dto.response.category.CategoryResponseDto;
import com.darong.malgage_api.controller.dto.response.emotion.EmotionResponseDto;
import com.darong.malgage_api.domain.category.CategoryType;
import com.darong.malgage_api.domain.record.PaymentMethod;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.repository.category.CategoryQueryRepository;
import com.darong.malgage_api.repository.emotion.EmotionQueryRepository;
import com.darong.malgage_api.external.gpt.util.PromptTemplates;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptTemplateService {

    private final CategoryQueryRepository categoryQueryRepository;
    private final EmotionQueryRepository emotionQueryRepository;
    private final ObjectMapper objectMapper;

    @Cacheable(value = "prompt:system", key = "'user:' + #user.id", unless = "#result == null")
    public String buildSystemPrompt(User user) {
        try {
            String categoryJson = objectMapper.writeValueAsString(getAllVisibleCategories(user));
            String emotionJson = objectMapper.writeValueAsString(getAllVisibleEmotions(user));
            String paymentMethodsJson = objectMapper.writeValueAsString(getPaymentMethods());

            return PromptTemplates.systemPrompt(categoryJson, emotionJson, paymentMethodsJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("프롬프트 JSON 직렬화 실패", e);
        }
    }

    private List<Map<String, Object>> getAllVisibleCategories(User user) {
        List<CategoryResponseDto> expenseCats = categoryQueryRepository.findVisibleCategoriesByUserAndType(user, CategoryType.EXPENSE);
        List<CategoryResponseDto> incomeCats = categoryQueryRepository.findVisibleCategoriesByUserAndType(user, CategoryType.INCOME);

        List<CategoryResponseDto> all = new ArrayList<>();
        all.addAll(expenseCats);
        all.addAll(incomeCats);

        all.sort(Comparator.comparing(CategoryResponseDto::getType)
                .thenComparing(CategoryResponseDto::getSortOrder));

        return all.stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId());
                    map.put("name", c.getName());
                    map.put("type", c.getType().name().toLowerCase());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getAllVisibleEmotions(User user) {
        List<EmotionResponseDto> emotions = emotionQueryRepository.findVisibleEmotionsByUser(user);

        emotions.sort(Comparator.comparing(EmotionResponseDto::getSortOrder));

        return emotions.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", e.getId());
                    map.put("name", e.getName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private List<String> getPaymentMethods() {
        return Arrays.stream(PaymentMethod.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
