package com.darong.malgage_api.repository.emotion;

import com.darong.malgage_api.controller.dto.response.emotion.EmotionResponseDto;
import com.darong.malgage_api.controller.dto.response.emotion.QEmotionResponseDto;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.EmotionScope;
import com.darong.malgage_api.domain.emotion.QEmotion;
import com.darong.malgage_api.domain.emotion.QUserEmotionVisibility;
import com.darong.malgage_api.domain.user.User;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.darong.malgage_api.domain.emotion.QEmotion.emotion;

/**
 * Emotion 관련 복잡한 쿼리를 위한 QueryDSL Repository
 */
@Repository
@RequiredArgsConstructor
public class EmotionQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자의 모든 감정 조회 (기본 + 커스텀)
     * - 기본 감정: 모든 사용자가 접근 가능
     * - 커스텀 감정: 해당 사용자 소유분만
     *
     * 복잡한 OR 조건이므로 QueryDSL로 처리
     */
    public List<Emotion> findAllEmotionForUser(Long userId) {
        return queryFactory
                .selectFrom(emotion)
                .where(
                        // 기본 감정는 모든 사용자가 볼 수 있음
                        emotion.scope.eq(EmotionScope.DEFAULT)
                                // 또는 사용자의 커스텀 감정
                                .or(emotion.scope.eq(EmotionScope.CUSTOM)
                                        .and(emotion.user.id.eq(userId)))
                )
                .orderBy(
                        emotion.scope.asc(),
                        emotion.sortOrder.asc()
                )
                .fetch();
    }

    public List<EmotionResponseDto> findDefaultEmotionsWithVisibility(User user) {
        QEmotion emotion = QEmotion.emotion;
        QUserEmotionVisibility visibility = QUserEmotionVisibility.userEmotionVisibility;

        return queryFactory
                .select(new QEmotionResponseDto(
                        emotion.id,
                        emotion.name,
                        emotion.sortOrder,
                        emotion.scope,
                        emotion.iconName,
                        emotion.scope.eq(EmotionScope.DEFAULT),
                        emotion.scope.eq(EmotionScope.CUSTOM),
                        Expressions.nullExpression(Long.class),
                        emotion.createdAt,
                        emotion.updatedAt,
                        visibility.isVisible.coalesce(true) // null일 때 기본값 true
                ))
                .from(emotion)
                .leftJoin(visibility)
                // ✅ 엔티티 참조 대신 FK 컬럼으로 직접 조인 (지연 로딩 방지)
                .on(visibility.emotion.id.eq(emotion.id)    // category 엔티티 접근 대신 id 사용
                        .and(visibility.user.id.eq(user.getId()))) // user 엔티티 접근 대신 id 사용
                .where(
                        emotion.scope.eq(EmotionScope.DEFAULT)
                )
                .orderBy(emotion.sortOrder.asc())
                .fetch();
    }

    public List<EmotionResponseDto> findCustomEmotionsWithVisibility(User user) {
        QEmotion emotion = QEmotion.emotion;
        QUserEmotionVisibility visibility = QUserEmotionVisibility.userEmotionVisibility;

        return queryFactory
                .select(new QEmotionResponseDto(
                        emotion.id,
                        emotion.name,
                        emotion.sortOrder,
                        emotion.scope,
                        emotion.iconName,
                        Expressions.FALSE, // isDefault = false (커스텀이므로)
                        Expressions.TRUE,  // isCustom = true
                        Expressions.constant(user.getId()), // ✅ 상수로 user ID 직접 설정
                        emotion.createdAt,
                        emotion.updatedAt,
                        visibility.isVisible.coalesce(true) // 기본값 true
                ))
                .from(emotion)
                .leftJoin(visibility)
                .on(visibility.emotion.id.eq(emotion.id)
                        .and(visibility.user.id.eq(user.getId())))
                .where(
                        emotion.scope.eq(EmotionScope.CUSTOM),
                        // ✅ FK 컬럼으로 직접 필터링 (Category 테이블의 user_id 컬럼)
                        emotion.user.id.eq(user.getId()) // 이건 단순 where 조건이라 문제없음
                )
                .orderBy(emotion.sortOrder.asc())
                .fetch();
    }

    public List<EmotionResponseDto> findVisibleEmotionsByUser(User user) {
        QEmotion emotion = QEmotion.emotion;
        QUserEmotionVisibility visibility = QUserEmotionVisibility.userEmotionVisibility;

        return queryFactory
                .select(new QEmotionResponseDto(
                        emotion.id,
                        emotion.name,
                        emotion.sortOrder,
                        emotion.scope,
                        emotion.iconName,
                        emotion.scope.eq(EmotionScope.DEFAULT),
                        emotion.scope.eq(EmotionScope.CUSTOM),
                        emotion.user.id,
                        emotion.createdAt,
                        emotion.updatedAt,
                        visibility.isVisible.coalesce(true)  // 가시성 설정이 없으면 true 처리
                ))
                .from(emotion)
                .leftJoin(visibility)
                .on(visibility.emotion.id.eq(emotion.id)
                        .and(visibility.user.id.eq(user.getId())))
                .where(
                        visibility.isVisible.isNull().or(visibility.isVisible.isTrue())  // ← 핵심 조건
                )
                .orderBy(emotion.sortOrder.asc())
                .fetch();
    }
}