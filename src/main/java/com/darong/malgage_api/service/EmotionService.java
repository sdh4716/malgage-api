package com.darong.malgage_api.service;

import com.darong.malgage_api.controller.dto.request.category.CategoryVisibilityRequestDto;
import com.darong.malgage_api.controller.dto.request.emotion.EmotionRequestDto;
import com.darong.malgage_api.controller.dto.request.emotion.EmotionVisibilityRequestDto;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.EmotionScope;
import com.darong.malgage_api.controller.dto.response.emotion.EmotionResponseDto;
import com.darong.malgage_api.domain.emotion.UserEmotionVisibility;
import com.darong.malgage_api.global.exception.NotFoundException;
import com.darong.malgage_api.repository.emotion.EmotionQueryRepository;
import com.darong.malgage_api.repository.emotion.EmotionRepository;
import com.darong.malgage_api.repository.emotion.UserEmotionVisibilityRepository;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

        if (scope == EmotionScope.DEFAULT) {
            return emotionQueryRepository.findDefaultEmotionsWithVisibility(user);
        } else {
            // 커스텀 감정 조회 - 해당 사용자만 볼 수 있음
            return emotionQueryRepository.findCustomEmotionsWithVisibility(user);
        }
    }

    public List<EmotionResponseDto> getVisibleEmotions(User user) {
        return emotionQueryRepository.findVisibleEmotionsByUser(user);
    }

    /**
     * 감정 등록
     */
    @Transactional
    public EmotionResponseDto createCustomEmotion(User user, EmotionRequestDto dto) {
        Emotion emotion = Emotion.createCustom(
                dto.getName(),
                dto.getIconName(),
                user,
                dto.getSortOrder()
        );

        Emotion saved = emotionRepository.save(emotion);
        return EmotionResponseDto.from(saved);
    }

    /**
     * 카테고리 가시성 설정
     */
    @Transactional
    public void updateVisibility(User user, EmotionVisibilityRequestDto dto) {
        Emotion emotion = emotionRepository.findById(dto.getEmotionId())
                .orElseThrow(() -> new NotFoundException("감정을 찾을 수 없습니다."));

        // 본인이 소유하거나 기본 카테고리인 경우만 허용
        if (emotion.isCustomEmotion() && !emotion.belongsToUser(user.getId())) {
            throw new AccessDeniedException("해당 감정에 대한 권한이 없습니다.");
        }

        UserEmotionVisibility visibility = visibilityRepository.findByUser_IdAndEmotion_Id(user.getId(), dto.getEmotionId())
                .orElseGet(() -> UserEmotionVisibility.createVisible(user, emotion)); // 없으면 생성

        if (dto.getVisible() != null) {
            if (dto.getVisible()) {
                visibility.show();
            } else {
                visibility.hide();
            }
        }

        visibilityRepository.save(visibility);
    }

    @Transactional
    public void deleteCustomEmotion(User user, Long emotionId) {
        Emotion emotion = emotionRepository.findById(emotionId)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다."));

        if (!emotion.isCustomEmotion()) {
            throw new IllegalArgumentException("사용자 정의 카테고리만 삭제할 수 있습니다.");
        }

        if (!emotion.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인의 카테고리만 삭제할 수 있습니다.");
        }

        emotionRepository.delete(emotion);
    }

}