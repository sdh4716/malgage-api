package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.domain.category.UserCategory;
import com.darong.malgage_api.domain.emotion.UserEmotion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    private int amount;

    @Enumerated(EnumType.STRING)
    private RecordType type;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_category_id", nullable = false)
    private UserCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_emotion_id", nullable = false)
    private UserEmotion emotion;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private boolean isInstallment;

    private int installmentMonths;

    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ 생성 메서드
    public static Record create(int amount,
                                RecordType type,
                                LocalDate date,
                                UserCategory category,
                                UserEmotion emotion,
                                PaymentMethod paymentMethod,
                                boolean isInstallment,
                                int installmentMonths,
                                String memo,
                                User user) {
        Record record = new Record();
        record.amount = amount;
        record.type = type;
        record.date = date;
        record.category = category;
        record.emotion = emotion;
        record.paymentMethod = paymentMethod;
        record.isInstallment = isInstallment;
        record.installmentMonths = installmentMonths;
        record.memo = memo;
        record.user = user;
        return record;
    }

    // ✅ 수정 메서드
    public void update(int amount,
                       RecordType type,
                       LocalDate date,
                       UserCategory category,
                       UserEmotion emotion,
                       PaymentMethod paymentMethod,
                       boolean installment,
                       int installmentMonths,
                       String memo) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.category = category;
        this.emotion = emotion;
        this.paymentMethod = paymentMethod;
        this.isInstallment = installment;
        this.installmentMonths = installmentMonths;
        this.memo = memo;
    }
}
