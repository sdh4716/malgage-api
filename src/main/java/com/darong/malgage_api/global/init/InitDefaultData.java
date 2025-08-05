package com.darong.malgage_api.global.init;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.CategoryType;
import com.darong.malgage_api.repository.category.CategoryRepository;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.repository.emotion.EmotionRepository;
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
     * 기본 카테고리 초기화 (아이콘 포함)
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

        // LinkedHashMap으로 순서 보장 <카테고리명, [타입, 아이콘]>
        Map<String, CategoryData> defaultCategories = new LinkedHashMap<>();

        // 🍽️ 지출 카테고리 (sortOrder 1~14)
        defaultCategories.put("식비", new CategoryData(CategoryType.EXPENSE, "restaurant"));
        defaultCategories.put("카페/간식", new CategoryData(CategoryType.EXPENSE, "local_cafe"));
        defaultCategories.put("교통", new CategoryData(CategoryType.EXPENSE, "directions_bus"));
        defaultCategories.put("쇼핑", new CategoryData(CategoryType.EXPENSE, "shopping_cart"));
        defaultCategories.put("문화생활", new CategoryData(CategoryType.EXPENSE, "movie"));
        defaultCategories.put("건강/병원", new CategoryData(CategoryType.EXPENSE, "local_hospital"));
        defaultCategories.put("교육/학원", new CategoryData(CategoryType.EXPENSE, "school"));
        defaultCategories.put("통신비", new CategoryData(CategoryType.EXPENSE, "phone_android"));
        defaultCategories.put("주거비", new CategoryData(CategoryType.EXPENSE, "home"));
        defaultCategories.put("공과금", new CategoryData(CategoryType.EXPENSE, "receipt_long"));
        defaultCategories.put("여행", new CategoryData(CategoryType.EXPENSE, "flight"));
        defaultCategories.put("구독서비스", new CategoryData(CategoryType.EXPENSE, "subscriptions"));
        defaultCategories.put("반려동물", new CategoryData(CategoryType.EXPENSE, "pets"));
        defaultCategories.put("경조사", new CategoryData(CategoryType.EXPENSE, "card_giftcard"));

        // 💰 수입 카테고리 (sortOrder 1~5)
        defaultCategories.put("월급", new CategoryData(CategoryType.INCOME, "paid"));
        defaultCategories.put("용돈", new CategoryData(CategoryType.INCOME, "savings"));
        defaultCategories.put("보너스", new CategoryData(CategoryType.INCOME, "celebration"));
        defaultCategories.put("환급/캐시백", new CategoryData(CategoryType.INCOME, "account_balance"));
        defaultCategories.put("기타수입", new CategoryData(CategoryType.INCOME, "monetization_on"));

        // 타입별 sortOrder 관리
        int expenseSortOrder = 1;
        int incomeSortOrder = 1;

        for (Map.Entry<String, CategoryData> entry : defaultCategories.entrySet()) {
            String name = entry.getKey();
            CategoryData data = entry.getValue();
            CategoryType type = data.type;
            String iconName = data.iconName;

            int sortOrder = (type == CategoryType.EXPENSE) ? expenseSortOrder++ : incomeSortOrder++;
            Category category = Category.createDefault(name, type, iconName, sortOrder);
            categoryRepository.save(category);
            log.debug("기본 카테고리 생성: {} ({}) - 아이콘: {}", name, type.getDescription(), iconName);
        }

        log.info("기본 카테고리 초기화가 완료되었습니다. 지출: {}개, 수입: {}개",
                expenseSortOrder - 1, incomeSortOrder - 1);
    }

    /**
     * 기본 감정 초기화 (아이콘 포함)
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

        // 감정과 아이콘 매핑
        Map<String, String> defaultEmotions = new LinkedHashMap<>();

        // 😊 긍정적 감정
        defaultEmotions.put("기쁨", "sentiment_very_satisfied");
        defaultEmotions.put("만족", "sentiment_satisfied");
        defaultEmotions.put("설렘", "favorite");
        defaultEmotions.put("행복", "mood");

        // 😰 부정적 감정
        defaultEmotions.put("스트레스", "sentiment_stressed");
        defaultEmotions.put("후회", "sentiment_very_dissatisfied");
        defaultEmotions.put("짜증", "sentiment_dissatisfied");
        defaultEmotions.put("분노", "sentiment_extremely_dissatisfied");
        defaultEmotions.put("슬픔", "sentiment_sad");
        defaultEmotions.put("허무함", "sentiment_neutral");

        // 😐 중립적 감정
        defaultEmotions.put("무감정", "sentiment_neutral");
        defaultEmotions.put("평범함", "sentiment_calm");

        int sortOrder = 1;
        int createdCount = 0;

        for (Map.Entry<String, String> entry : defaultEmotions.entrySet()) {
            String name = entry.getKey();
            String iconName = entry.getValue();

            Emotion emotion = Emotion.createDefault(name, iconName, sortOrder);
            emotionRepository.save(emotion);
            createdCount++;
            log.debug("기본 감정 생성: {} - 아이콘: {}", name, iconName);

            sortOrder++;
        }

        log.info("기본 감정 초기화가 완료되었습니다. 생성된 감정: {}개", createdCount);
    }

    /**
     * 카테고리 데이터 래퍼 클래스
     */
    private static class CategoryData {
        final CategoryType type;
        final String iconName;

        CategoryData(CategoryType type, String iconName) {
            this.type = type;
            this.iconName = iconName;
        }
    }
}