package com.darong.malgage_api.repository.record;

import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.darong.malgage_api.domain.record.QRecord.record;
import static com.darong.malgage_api.domain.category.QCategory.category;
import static com.darong.malgage_api.domain.emotion.QEmotion.emotion;
import static com.darong.malgage_api.domain.record.QInstallmentSchedule. installmentSchedule;

@Repository
@RequiredArgsConstructor
public class RecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자의 기간별 가계부 기록 조회
     * "DTO에서 category, emotion 필드를 안전하게 사용하기 위해,
     * 해당 연관 객체들을 fetchJoin으로 미리 조회하여 LazyInitializationException을 방지한다."
     */
    public List<Record> findRecordsByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .selectFrom(record)
                .join(record.category, category).fetchJoin()
                .join(record.emotion, emotion).fetchJoin()
                .where(
                        record.user.eq(user)
                                .and(record.date.between(start, end))
                                .and(record.isInstallment.eq(false)) // 할부는 제외
                )
                .fetch();
    }

}

