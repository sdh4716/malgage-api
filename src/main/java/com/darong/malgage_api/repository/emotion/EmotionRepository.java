package com.darong.malgage_api.repository.emotion;

import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.EmotionScope;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    /**
     * 기본 카테고리만 조회 (타입별, 정렬순서로 정렬)
     */
    List<Emotion> findByScopeOrderBySortOrderAsc(EmotionScope scope);

    /**
     * 사용자의 커스텀 카테고리만 조회 (타입별, 정렬순서로 정렬)
     */
    List<Emotion> findByUserIdAndScopeOrderBySortOrderAsc(Long userId, EmotionScope scope);
}