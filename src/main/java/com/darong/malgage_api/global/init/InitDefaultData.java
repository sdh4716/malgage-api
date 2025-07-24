package com.darong.malgage_api.global.init;

import com.darong.malgage_api.domain.category.CategoryDefault;
import com.darong.malgage_api.domain.category.CategoryType;
import com.darong.malgage_api.domain.category.repository.CategoryDefaultRepository;
import com.darong.malgage_api.domain.emotion.EmotionDefault;
import com.darong.malgage_api.domain.emotion.repository.EmotionDefaultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InitDefaultData implements CommandLineRunner {

    private final CategoryDefaultRepository categoryDefaultRepository;
    private final EmotionDefaultRepository emotionDefaultRepository;

    @Override
    public void run(String... args) {
        initDefaultCategories();
        initDefaultEmotions();
    }

    private void initDefaultCategories() {
        Map<String, CategoryType> defaultCategories = Map.ofEntries(
                Map.entry("식비", CategoryType.EXPENSE),
                Map.entry("카페/간식", CategoryType.EXPENSE),
                Map.entry("교통", CategoryType.EXPENSE),
                Map.entry("쇼핑", CategoryType.EXPENSE),
                Map.entry("문화생활", CategoryType.EXPENSE),
                Map.entry("건강/병원", CategoryType.EXPENSE),
                Map.entry("교육/학원", CategoryType.EXPENSE),
                Map.entry("통신비", CategoryType.EXPENSE),
                Map.entry("주거비", CategoryType.EXPENSE),
                Map.entry("공과금", CategoryType.EXPENSE),
                Map.entry("여행", CategoryType.EXPENSE),
                Map.entry("구독서비스", CategoryType.EXPENSE),
                Map.entry("반려동물", CategoryType.EXPENSE),
                Map.entry("경조사", CategoryType.EXPENSE),
                Map.entry("월급", CategoryType.INCOME),
                Map.entry("용돈", CategoryType.INCOME),
                Map.entry("보너스", CategoryType.INCOME),
                Map.entry("환급/캐시백", CategoryType.INCOME),
                Map.entry("기타수입", CategoryType.INCOME)
        );

        int sortOrder = 1;
        for (Map.Entry<String, CategoryType> entry : defaultCategories.entrySet()) {
            String name = entry.getKey();
            CategoryType type = entry.getValue();

            if (!categoryDefaultRepository.existsByName(name)) {
                categoryDefaultRepository.save(CategoryDefault.create(name, sortOrder++, type));
            }
        }
    }

    private void initDefaultEmotions() {
        List<String> defaultEmotions = List.of(
                "기쁨", "만족", "설렘", "행복",
                "스트레스", "후회", "짜증", "분노", "슬픔", "허무함",
                "무감정", "평범함"
        );

        int sortOrder = 1;
        for (String name : defaultEmotions) {
            if (!emotionDefaultRepository.existsByName(name)) {
                emotionDefaultRepository.save(EmotionDefault.create(name, sortOrder++));
            }
        }
    }
}
