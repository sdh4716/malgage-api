package com.darong.malgage_api.repository.record;

import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.darong.malgage_api.domain.record.QRecord.record;
import static com.darong.malgage_api.domain.category.QCategory.category;
import static com.darong.malgage_api.domain.emotion.QEmotion.emotion;

@Repository
@RequiredArgsConstructor
public class RecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자의 기간별 가계부 기록 조회
     * "DTO에서 category, emotion 필드를 안전하게 사용하기 위해,
     * 해당 연관 객체들을 fetchJoin으로 미리 조회하여 LazyInitializationException을 방지한다."
     */
    public List<Record> findWithCategoryAndEmotionByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(record)
                .join(record.category, category).fetchJoin()
                .join(record.emotion, emotion).fetchJoin()
                .where(
                        record.user.eq(user)
                                .and(record.date.between(startDate, endDate))
                )
                .orderBy(record.date.desc())
                .fetch();
    }
}
