package com.darong.malgage_api.domain.record.repository;

import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.darong.malgage_api.domain.record.QRecord.record;

@Repository
@RequiredArgsConstructor
public class RecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자의 기간별 가계부 기록 조회
     */
    public List<Record> findByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(record)
                .where(
                        record.user.eq(user)
                                .and(record.date.between(startDate, endDate))
                )
                .orderBy(record.date.desc())
                .fetch();
    }
}
