// domain/record/Record.java
package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_record_user_date", columnList = "user_id, date"),
        @Index(name = "idx_record_user_category", columnList = "user_id, category_id"),
        @Index(name = "idx_record_user_type", columnList = "user_id, type")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RecordType type;

    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * 카테고리 참조 (기본 또는 커스텀)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * 감정 참조 (기본 또는 커스텀)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "is_installment", nullable = false)
    private Boolean isInstallment = false;

    @Column(name = "installment_months")
    private Integer installmentMonths;

    @Column(length = 500)
    private String memo;

    /**
     * 기록 소유자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ===== private 생성자 =====
    private Record(Integer amount, RecordType type, LocalDateTime date, Category category,
                   Emotion emotion, PaymentMethod paymentMethod, Boolean isInstallment,
                   Integer installmentMonths, String memo, User user) {
        validateAmount(amount);
        validateType(type);
        validateDate(date);
        validateCategory(category);
        validateEmotion(emotion);
        validateUser(user);

        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
        this.emotion = emotion;
        this.paymentMethod = paymentMethod;
        this.isInstallment = isInstallment != null ? isInstallment : false;
        this.installmentMonths = installmentMonths;
        this.memo = memo;
        this.user = user;
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 가계부 기록 생성
     */
    public static Record create(Integer amount, RecordType type, LocalDateTime date,
                                Category category, Emotion emotion, PaymentMethod paymentMethod,
                                Boolean isInstallment, Integer installmentMonths, String memo, User user) {
        return new Record(amount, type, date, category, emotion, paymentMethod,
                isInstallment, installmentMonths, memo, user);
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 가계부 기록 수정
     */
    public void update(Integer amount, RecordType type, LocalDateTime date, Category category,
                       Emotion emotion, PaymentMethod paymentMethod, Boolean isInstallment,
                       Integer installmentMonths, String memo) {
        if (amount != null) {
            validateAmount(amount);
            this.amount = amount;
        }
        if (type != null) {
            this.type = type;
        }
        if (date != null) {
            validateDate(date);
            this.date = date;
        }
        if (category != null) {
            validateCategory(category);
            this.category = category;
        }
        if (emotion != null) {
            validateEmotion(emotion);
            this.emotion = emotion;
        }
        if (paymentMethod != null) {
            this.paymentMethod = paymentMethod;
        }
        if (isInstallment != null) {
            this.isInstallment = isInstallment;
        }
        if (installmentMonths != null) {
            this.installmentMonths = installmentMonths;
        }
        if (memo != null) {
            this.memo = memo;
        }
    }

    /**
     * 특정 사용자의 기록인지 확인
     */
    public boolean belongsToUser(Long userId) {
        return this.user != null && this.user.getId().equals(userId);
    }

    /**
     * 수입 기록인지 확인
     */
    public boolean isIncome() {
        return this.type == RecordType.INCOME;
    }

    /**
     * 지출 기록인지 확인
     */
    public boolean isExpense() {
        return this.type == RecordType.EXPENSE;
    }

    /**
     * 할부 결제인지 확인
     */
    public boolean hasInstallment() {
        return this.isInstallment != null && this.isInstallment;
    }

    /**
     * 메모가 있는지 확인
     */
    public boolean hasMemo() {
        return this.memo != null && !this.memo.trim().isEmpty();
    }

    // ===== 유효성 검증 메서드 =====

    private static void validateAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
    }

    private static void validateType(RecordType type) {
        if (type == null) {
            throw new IllegalArgumentException("기록 타입은 필수입니다.");
        }
    }

    private static void validateDate(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수입니다.");
        }
        if (date.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("미래 날짜는 입력할 수 없습니다.");
        }
    }

    private static void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }
    }

    private static void validateEmotion(Emotion emotion) {
        if (emotion == null) {
            throw new IllegalArgumentException("감정은 필수입니다.");
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }
    }

    // ===== Object 메서드 오버라이드 =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Record record = (Record) obj;
        return id != null && id.equals(record.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Record{id=%d, amount=%d, type=%s, date=%s}",
                id, amount, type, date);
    }
}