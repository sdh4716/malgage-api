package com.darong.malgage_api.global.init;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.CategoryType;
import com.darong.malgage_api.domain.category.repository.CategoryRepository;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitDefaultData implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final EmotionRepository emotionRepository;

    @Override
    public void run(String... args) {
        log.info("기본 데이터 초기화를 시작합니다.");
        initDefaultCategories();
        initDefaultEmotions();
        log.info("기본 데이터 초기화가 완료되었습니다.");
    }

    /**
     * 기본 카테고리 초기화
     * - 모든 사용자에게 제공되는 기본 카테고리들을 생성
     * - 이미 존재하는 카테고리는 건너뜀
     */
    private void initDefaultCategories() {
        // 기본 카테고리가 이미 존재하면 초기화 건너뛰기
        long existingCount = categoryRepository.count();
        if (existingCount > 0) {
            log.info("기본 카테고리가 이미 {}개 존재합니다. 초기화를 건너뜁니다.", existingCount);
            return;
        }

        log.info("기본 카테고리 초기화를 시작합니다.");

        // LinkedHashMap으로 순서 보장
        Map<String, CategoryType> defaultCategories = new LinkedHashMap<>();

        // 지출 카테고리 (sortOrder 1~14)
        defaultCategories.put("식비", CategoryType.EXPENSE);
        defaultCategories.put("카페/간식", CategoryType.EXPENSE);
        defaultCategories.put("교통", CategoryType.EXPENSE);
        defaultCategories.put("쇼핑", CategoryType.EXPENSE);
        defaultCategories.put("문화생활", CategoryType.EXPENSE);
        defaultCategories.put("건강/병원", CategoryType.EXPENSE);
        defaultCategories.put("교육/학원", CategoryType.EXPENSE);
        defaultCategories.put("통신비", CategoryType.EXPENSE);
        defaultCategories.put("주거비", CategoryType.EXPENSE);
        defaultCategories.put("공과금", CategoryType.EXPENSE);
        defaultCategories.put("여행", CategoryType.EXPENSE);
        defaultCategories.put("구독서비스", CategoryType.EXPENSE);
        defaultCategories.put("반려동물", CategoryType.EXPENSE);
        defaultCategories.put("경조사", CategoryType.EXPENSE);

        // 수입 카테고리 (sortOrder 1~5)
        defaultCategories.put("월급", CategoryType.INCOME);
        defaultCategories.put("용돈", CategoryType.INCOME);
        defaultCategories.put("보너스", CategoryType.INCOME);
        defaultCategories.put("환급/캐시백", CategoryType.INCOME);
        defaultCategories.put("기타수입", CategoryType.INCOME);

        // 타입별 sortOrder 관리
        int expenseSortOrder = 1;
        int incomeSortOrder = 1;

        for (Map.Entry<String, CategoryType> entry : defaultCategories.entrySet()) {
            String name = entry.getKey();
            CategoryType type = entry.getValue();


            int sortOrder = (type == CategoryType.EXPENSE) ? expenseSortOrder++ : incomeSortOrder++;
            Category category = Category.createDefault(name, type, sortOrder);
            categoryRepository.save(category);
            log.debug("기본 카테고리 생성: {} ({})", name, type.getDescription());

        }

        log.info("기본 카테고리 초기화가 완료되었습니다. 지출: {}개, 수입: {}개",
                expenseSortOrder - 1, incomeSortOrder - 1);
    }

    /**
     * 기본 감정 초기화
     * - 가계부 기록 시 사용할 기본 감정들을 생성
     * - 이미 존재하는 감정은 건너뜀
     */
    private void initDefaultEmotions() {
        // 기본 감정이 이미 존재하면 초기화 건너뛰기
        long existingCount = emotionRepository.count();
        if (existingCount > 0) {
            log.info("기본 감정이 이미 {}개 존재합니다. 초기화를 건너뜁니다.", existingCount);
            return;
        }

        log.info("기본 감정 초기화를 시작합니다.");

        List<String> defaultEmotions = List.of(
                // 긍정적 감정
                "기쁨", "만족", "설렘", "행복",
                // 부정적 감정
                "스트레스", "후회", "짜증", "분노", "슬픔", "허무함",
                // 중립적 감정
                "무감정", "평범함"
        );

        int sortOrder = 1;
        int createdCount = 0;

        for (String name : defaultEmotions) {

            Emotion emotion = Emotion.createDefault(name, sortOrder);
            emotionRepository.save(emotion);
            createdCount++;
            log.debug("기본 감정 생성: {}", name);

            sortOrder++;
        }

        log.info("기본 감정 초기화가 완료되었습니다. 생성된 감정: {}개", createdCount);
    }
}