package com.darong.malgage_api.repository.statistics;

import com.darong.malgage_api.controller.dto.response.statistics.*;
import com.darong.malgage_api.domain.record.RecordType;
import com.darong.malgage_api.domain.statistics.StatisticsPeriod;
import com.darong.malgage_api.domain.user.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.darong.malgage_api.domain.record.QRecord.record;
import static com.darong.malgage_api.domain.category.QCategory.category;
import static com.darong.malgage_api.domain.record.QInstallmentSchedule.installmentSchedule;
import static com.darong.malgage_api.domain.emotion.QEmotion.emotion;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StatisticsQueryRepository {

    private final JPAQueryFactory query;

    public StatisticsResponseDto getStatistics(User user, StatisticsPeriod period, int year, Integer month) {

        // 기간 계산
        LocalDateTime start, end, prevStart, prevEnd;

        if (period == StatisticsPeriod.MONTHLY) {
            YearMonth ym = YearMonth.of(year, month);
            start = ym.atDay(1).atStartOfDay();
            end   = ym.atEndOfMonth().atTime(23, 59, 59);
            YearMonth prev = ym.minusMonths(1);
            prevStart = prev.atDay(1).atStartOfDay();
            prevEnd   = prev.atEndOfMonth().atTime(23, 59, 59);
        } else { // YEARLY
            start = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            end   = LocalDateTime.of(year, 12, 31, 23, 59, 59);
            prevStart = LocalDateTime.of(year - 1, 1, 1, 0, 0, 0);
            prevEnd   = LocalDateTime.of(year - 1, 12, 31, 23, 59, 59);
        }

        // 개요(기간/직전기간) — 일반 지출 + 할부 월금
        int totalIncome   = sumRecord(user, start, end, RecordType.INCOME, false);
        int normalExpense = sumRecord(user, start, end, RecordType.EXPENSE, false);
        int instExpense   = sumInstallmentMonthlyAmount(user, start, end);
        int totalExpense  = normalExpense + instExpense;

        int lastNormalExpense = sumRecord(user, prevStart, prevEnd, RecordType.EXPENSE, false);
        int lastInstExpense   = sumInstallmentMonthlyAmount(user, prevStart, prevEnd);
        int lastPeriodExpense = lastNormalExpense + lastInstExpense;

        int netIncome = totalIncome - totalExpense;
        double changePercent = (lastPeriodExpense == 0) ? 0.0
                : ((totalExpense - lastPeriodExpense) * 100.0 / lastPeriodExpense);

        PeriodOverviewDto overview = new PeriodOverviewDto(
                totalIncome, totalExpense, lastPeriodExpense, netIncome, round1(changePercent)
        );

        // 나머지 섹션은 기존 로직 재사용/추후 확장
        BudgetProgressDto budget = null; // 연/월 통합 예산 로직은 이후 추가
        List<EmotionalSpendingDto> emotional = buildEmotionalSpending(user, start, end, totalExpense);
        List<CategorySpendingDto> category = buildCategorySpending(user, start, end, totalExpense);
        List<PaymentMethodSpendingDto> payment = Collections.emptyList();
        InstallmentSummaryDto installments = buildInstallmentSummary(user, start, end, totalIncome);
        List<InsightDto> insights = Collections.emptyList();

        return new StatisticsResponseDto(overview, budget, emotional, category, payment, installments, insights);
    }

    // ---------- 결제수단별 ----------
    private List<PaymentMethodSpendingDto> buildPaymentMethodSpending(User user, LocalDateTime start, LocalDateTime end, int totalExpense) {
        List<Tuple> normal = query
                .select(record.paymentMethod.stringValue(), record.amount.sum(), record.id.count())
                .from(record)
                .where(
                        record.user.eq(user),
                        record.type.eq(com.darong.malgage_api.domain.record.RecordType.EXPENSE),
                        record.isInstallment.eq(false),
                        record.date.between(start, end)
                )
                .groupBy(record.paymentMethod)
                .fetch();

        List<Tuple> inst = query
                .select(record.paymentMethod.stringValue(), installmentSchedule.monthlyAmount.sum(), installmentSchedule.id.count())
                .from(installmentSchedule)
                .join(installmentSchedule.record, record)
                .where(
                        record.user.eq(user),
                        installmentSchedule.scheduledDate.between(start, end)
                )
                .groupBy(record.paymentMethod)
                .fetch();

        class Agg { int amount; int count; }
        Map<String, Agg> map = new LinkedHashMap<>();
        normal.forEach(t -> {
            String key = t.get(record.paymentMethod.stringValue());
            Agg a = new Agg();
            a.amount = safeInt(t.get(record.amount.sum()));
            a.count  = safeLongToInt(t.get(record.id.count()));
            map.put(key, a);
        });
        inst.forEach(t -> {
            String key = t.get(record.paymentMethod.stringValue());
            Agg a = map.getOrDefault(key, new Agg());
            a.amount += safeInt(t.get(installmentSchedule.monthlyAmount.sum()));
            a.count  += safeLongToInt(t.get(installmentSchedule.id.count()));
            map.put(key, a);
        });

        return map.entrySet().stream()
                .map(e -> new PaymentMethodSpendingDto(
                        e.getKey(),
                        toKoreanPaymentName(e.getKey()),
                        e.getValue().amount,
                        totalExpense == 0 ? 0.0 : round1(e.getValue().amount * 100.0 / totalExpense),
                        e.getValue().count
                ))
                .collect(Collectors.toList());
    }


    // ---------- 감정별 ----------
    private List<EmotionalSpendingDto> buildEmotionalSpending(User user, LocalDateTime start, LocalDateTime end, int totalExpense) {
        // 1) 일반 지출 (isInstallment = false)
        List<Tuple> normal = query
                .select(emotion.id, emotion.name, emotion.iconName, record.amount.sum())
                .from(record)
                .join(record.emotion, emotion)
                .where(
                        record.user.eq(user),
                        record.type.eq(com.darong.malgage_api.domain.record.RecordType.EXPENSE),
                        record.isInstallment.eq(false),
                        record.date.between(start, end)
                )
                .groupBy(emotion.id, emotion.name, emotion.iconName)
                .fetch();

        // 2) 할부 지출(이번달 회차 금액)
        List<Tuple> inst = query
                .select(emotion.id, emotion.name, emotion.iconName, installmentSchedule.monthlyAmount.sum())
                .from(installmentSchedule)
                .join(installmentSchedule.record, record)
                .join(record.emotion, emotion)
                .where(
                        record.user.eq(user),
                        installmentSchedule.scheduledDate.between(start, end)
                )
                .groupBy(emotion.id, emotion.name, emotion.iconName)
                .fetch();

        Map<Long, EmotionalSpendingDto> map = new LinkedHashMap<>();
        normal.forEach(t -> {
            Long id = t.get(emotion.id);
            int amt = safeInt(t.get(record.amount.sum()));
            map.put(id, new EmotionalSpendingDto(id, t.get(emotion.name), t.get(emotion.iconName), amt, 0.0));
        });
        inst.forEach(t -> {
            Long id = t.get(emotion.id);
            int add = safeInt(t.get(installmentSchedule.monthlyAmount.sum()));
            map.compute(id, (k, v) -> {
                if (v == null) return new EmotionalSpendingDto(id, t.get(emotion.name), t.get(emotion.iconName), add, 0.0);
                return new EmotionalSpendingDto(id, v.getEmotionName(), v.getEmotionIcon(), v.getAmount() + add, 0.0);
            });
        });

        return map.values().stream()
                .map(v -> new EmotionalSpendingDto(
                        v.getEmotionId(), v.getEmotionName(), v.getEmotionIcon(),
                        v.getAmount(),
                        totalExpense == 0 ? 0.0 : round1(v.getAmount() * 100.0 / totalExpense)
                ))
                .collect(Collectors.toList());
    }


    // ---------- 카테고리별 ----------
    private List<CategorySpendingDto> buildCategorySpending(User user, LocalDateTime start, LocalDateTime end, int totalExpense) {
        List<Tuple> normal = query
                .select(category.id, category.name, category.iconName, record.amount.sum(), record.id.count())
                .from(record)
                .join(record.category, category)
                .where(
                        record.user.eq(user),
                        record.type.eq(com.darong.malgage_api.domain.record.RecordType.EXPENSE),
                        record.isInstallment.eq(false),
                        record.date.between(start, end)
                )
                .groupBy(category.id, category.name, category.iconName)
                .fetch();

        List<Tuple> inst = query
                .select(category.id, category.name, category.iconName,
                        installmentSchedule.monthlyAmount.sum(), installmentSchedule.id.count())
                .from(installmentSchedule)
                .join(installmentSchedule.record, record)
                .join(record.category, category)
                .where(
                        record.user.eq(user),
                        installmentSchedule.scheduledDate.between(start, end)
                )
                .groupBy(category.id, category.name, category.iconName)
                .fetch();

        class Agg { int amount; int count; String name; String icon; }
        Map<Long, Agg> map = new LinkedHashMap<>();

        normal.forEach(t -> {
            Long id = t.get(category.id);
            Agg a = new Agg();
            a.amount = safeInt(t.get(record.amount.sum()));
            a.count  = safeLongToInt(t.get(record.id.count()));
            a.name   = t.get(category.name);
            a.icon   = t.get(category.iconName);
            map.put(id, a);
        });

        inst.forEach(t -> {
            Long id = t.get(category.id);
            Agg a = map.getOrDefault(id, new Agg());
            a.amount += safeInt(t.get(installmentSchedule.monthlyAmount.sum()));
            a.count  += safeLongToInt(t.get(installmentSchedule.id.count()));
            a.name    = t.get(category.name);
            a.icon    = t.get(category.iconName);
            map.put(id, a);
        });

        return map.entrySet().stream()
                .map(e -> new CategorySpendingDto(
                        e.getKey(),
                        e.getValue().name,
                        e.getValue().icon,
                        e.getValue().amount,
                        totalExpense == 0 ? 0.0 : round1(e.getValue().amount * 100.0 / totalExpense),
                        e.getValue().count
                ))
                .collect(Collectors.toList());
    }

    // ---------- 할부 요약 ----------
    private InstallmentSummaryDto buildInstallmentSummary(User user, LocalDateTime start, LocalDateTime end, int totalIncome) {
        // 이번달 회차 목록
        List<Tuple> rows = query
                .select(
                        record.id,
                        record.memo,
                        record.amount,                        // 원금
                        record.installmentMonths,
                        installmentSchedule.installmentIndex,
                        installmentSchedule.monthlyAmount,
                        installmentSchedule.scheduledDate
                )
                .from(installmentSchedule)
                .join(installmentSchedule.record, record)
                .where(
                        record.user.eq(user),
                        installmentSchedule.scheduledDate.between(start, end)
                )
                .orderBy(installmentSchedule.scheduledDate.asc())
                .fetch();

        // 합계/건수
        Integer totalMonthly = query
                .select(installmentSchedule.monthlyAmount.sum())
                .from(installmentSchedule)
                .join(installmentSchedule.record, record)
                .where(
                        record.user.eq(user),
                        installmentSchedule.scheduledDate.between(start, end)
                )
                .fetchOne();
        int monthlyPayment = safeInt(totalMonthly);

        int activeCount = query
                .select(record.id.countDistinct())
                .from(installmentSchedule)
                .join(installmentSchedule.record, record)
                .where(
                        record.user.eq(user),
                        installmentSchedule.scheduledDate.between(start, end)
                )
                .fetchOne().intValue();

        double paymentRatio = (totalIncome == 0) ? 0.0 : round1(monthlyPayment * 100.0 / totalIncome);

        List<InstallmentDetailDto> details = rows.stream()
                .map(t -> new InstallmentDetailDto(
                        t.get(record.id),
                        buildInstallmentDescription(t.get(record.memo)), // 필요시 더 풍부하게
                        t.get(record.amount),
                        t.get(installmentSchedule.monthlyAmount),
                        t.get(installmentSchedule.installmentIndex),
                        t.get(record.installmentMonths),
                        t.get(installmentSchedule.installmentIndex) + "/" + t.get(record.installmentMonths),
                        t.get(installmentSchedule.scheduledDate)
                ))
                .collect(Collectors.toList());

        return new InstallmentSummaryDto(activeCount, monthlyPayment, paymentRatio, details);
    }

    // ===== 합계 유틸 =====
    private int sumRecord(com.darong.malgage_api.domain.user.User user,
                          LocalDateTime start, LocalDateTime end,
                          RecordType type, boolean includeInstallment) {
        Integer sum = query
                .select(record.amount.sum())
                .from(record)
                .where(
                        record.user.eq(user),
                        record.type.eq(type),
                        record.date.between(start, end),
                        includeInstallment ? null : record.isInstallment.eq(false)
                )
                .fetchOne();
        return sum == null ? 0 : sum;
    }

    private int sumInstallmentMonthlyAmount(com.darong.malgage_api.domain.user.User user,
                                            LocalDateTime start, LocalDateTime end) {
        Integer sum = query
                .select(installmentSchedule.monthlyAmount.sum())
                .from(installmentSchedule)
                .join(installmentSchedule.record, record)
                .where(
                        record.user.eq(user),
                        installmentSchedule.scheduledDate.between(start, end)
                )
                .fetchOne();
        return sum == null ? 0 : sum;
    }

    private String buildInstallmentDescription(String memo) {
        return (memo == null || memo.isBlank()) ? "할부 결제" : memo;
    }

    private int safeInt(Integer v) { return v == null ? 0 : v; }

    private int safeLongToInt(Long v) { return v == null ? 0 : v.intValue(); }

    private double round1(double v) { return Math.round(v * 10.0) / 10.0; }

    // ---------- 유틸 ----------
    private String toKoreanPaymentName(String method) {
        if (method == null) return "기타";
        switch (method) {
            case "CREDIT_CARD": return "신용카드";
            case "DEBIT_CARD":  return "체크카드";
            case "CASH":        return "현금";
            default:            return "기타";
        }
    }

}
