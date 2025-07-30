// domain/emotion/service/EmotionService.java
package com.darong.malgage_api.service;

import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.EmotionScope;
import com.darong.malgage_api.controller.dto.response.category.EmotionResponseDto;
import com.darong.malgage_api.repository.emotion.EmotionQueryRepository;
import com.darong.malgage_api.repository.emotion.EmotionRepository;
import com.darong.malgage_api.repository.emotion.UserEmotionVisibilityRepository;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final EmotionQueryRepository emotionQueryRepository;
    private final UserEmotionVisibilityRepository visibilityRepository;

    /**
     * 사용자의 모든 감정 조회 (기본 + 커스텀)
     * DB에서 필요한 데이터만 조회하여 성능 최적화
     */
    public List<EmotionResponseDto> getAllEmotions(User user) {
        List<Emotion> emotions = emotionQueryRepository.findAllEmotionForUser(user.getId());

        return emotions.stream()
                .map(EmotionResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * EmotionScope 기준 감정 조회
     * 스코프에 따라 적절한 Repository 메서드 선택
     */
    public List<EmotionResponseDto> getEmotionsByScope(User user, EmotionScope scope) {
        List<Emotion> emotions;

        if (scope == EmotionScope.DEFAULT) {
            // 기본 감정 조회 - 모든 사용자가 볼 수 있음
            emotions = emotionRepository.findByScopeOrderBySortOrderAsc(scope);
        } else {
            // 커스텀 감정 조회 - 해당 사용자만 볼 수 있음
            emotions = emotionRepository.findByUserIdAndScopeOrderBySortOrderAsc(user.getId(), scope);
        }

        return emotions.stream()
                .map(EmotionResponseDto::from)
                .collect(Collectors.toList());
    }

}