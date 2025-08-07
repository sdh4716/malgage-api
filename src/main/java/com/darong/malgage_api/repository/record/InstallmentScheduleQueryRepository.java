package com.darong.malgage_api.repository.record;

import com.darong.malgage_api.domain.record.InstallmentSchedule;
import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.darong.malgage_api.domain.category.QCategory.category;
import static com.darong.malgage_api.domain.emotion.QEmotion.emotion;
import static com.darong.malgage_api.domain.record.QInstallmentSchedule.installmentSchedule;
import static com.darong.malgage_api.domain.record.QRecord.record;

@Repository
@RequiredArgsConstructor
public class InstallmentScheduleQueryRepository {

    private final JPAQueryFactory queryFactory;


    /**
     * ✅ 특정 유저의 기간 내 할부 회차 정보 조회 (N+1 방지 fetchJoin 포함)
     */
    public List<InstallmentSchedule> findByUserAndScheduledDateBetween(User user, LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .selectFrom(installmentSchedule)
                .join(installmentSchedule.record, record).fetchJoin()
                .join(record.category, category).fetchJoin()
                .join(record.emotion, emotion).fetchJoin()
                .where(
                        record.user.eq(user),
                        installmentSchedule.scheduledDate.between(start, end)
                )
                .fetch();
    }
}
