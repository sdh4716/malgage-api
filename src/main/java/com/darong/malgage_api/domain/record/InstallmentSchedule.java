package com.darong.malgage_api.domain.record;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstallmentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    private int installmentIndex;           // 1부터 시작하는 회차 번호

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;        // 할부 납부 예정일 (예: 2025-09-12)

    private int monthlyAmount;              // 할부 월 금액

    // ✅ 정적 팩토리 메서드
    public static InstallmentSchedule create(Record record, int index, LocalDateTime scheduledDate, int monthlyAmount) {
        return new InstallmentSchedule(record, index, scheduledDate, monthlyAmount);
    }

    // ✅ private 생성자
    private InstallmentSchedule(Record record, int installmentIndex, LocalDateTime scheduledDate, int monthlyAmount) {
        this.record = record;
        this.installmentIndex = installmentIndex;
        this.scheduledDate = scheduledDate;
        this.monthlyAmount = monthlyAmount;
    }
}
