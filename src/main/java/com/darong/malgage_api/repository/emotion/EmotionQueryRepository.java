package com.darong.malgage_api.repository.emotion;

import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.EmotionScope;
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
}