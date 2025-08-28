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
                        emotion.isDeleted.isFalse(),   // ✅ 삭제되지 않은 감정만
                        emotion.scope.eq(EmotionScope.DEFAULT)
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
                        visibility.isVisible.coalesce(true)
                ))
                .from(emotion)
                .leftJoin(visibility)
                .on(visibility.emotion.id.eq(emotion.id)
                        .and(visibility.user.id.eq(user.getId())))
                .where(
                        emotion.isDeleted.isFalse(),   // ✅ 추가
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
                        Expressions.FALSE,
                        Expressions.TRUE,
                        Expressions.constant(user.getId()),
                        emotion.createdAt,
                        emotion.updatedAt,
                        visibility.isVisible.coalesce(true)
                ))
                .from(emotion)
                .leftJoin(visibility)
                .on(visibility.emotion.id.eq(emotion.id)
                        .and(visibility.user.id.eq(user.getId())))
                .where(
                        emotion.isDeleted.isFalse(),   // ✅ 추가
                        emotion.scope.eq(EmotionScope.CUSTOM),
                        emotion.user.id.eq(user.getId())
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
                        visibility.isVisible.coalesce(true)
                ))
                .from(emotion)
                .leftJoin(visibility)
                .on(visibility.emotion.id.eq(emotion.id)
                        .and(visibility.user.id.eq(user.getId())))
                .where(
                        emotion.isDeleted.isFalse(),   // ✅ 추가
                        visibility.isVisible.isNull().or(visibility.isVisible.isTrue())
                )
                .orderBy(emotion.sortOrder.asc())
                .fetch();
    }

}