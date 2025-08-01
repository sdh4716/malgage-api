// domain/emotion/UserEmotionVisibility.java
package com.darong.malgage_api.domain.emotion;

import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자별 감정 가시성 관리 엔티티
 * 기본 감정의 숨김/표시 상태만 관리
 */
@Entity
@Table(name = "user_emotion_visibility",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_emotion", columnNames = {"user_id", "emotion_id"})
        },
        indexes = {
                @Index(name = "idx_user_emotion_visible", columnList = "user_id, is_visible")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEmotionVisibility extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visibility_id")
    private Long id;

    /**
     * 사용자 참조
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 감정 참조
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    /**
     * 사용자에게 표시되는지 여부
     */
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    // ===== private 생성자 =====
    private UserEmotionVisibility(User user, Emotion emotion, Boolean isVisible) {
        validateUser(user);
        validateEmotion(emotion);

        this.user = user;
        this.emotion = emotion;
        this.isVisible = isVisible != null ? isVisible : true;
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 기본 가시성 설정 생성 (표시 상태)
     */
    public static UserEmotionVisibility createVisible(User user, Emotion emotion) {
        UserEmotionVisibility visibility = new UserEmotionVisibility(user, emotion, true);

        return visibility;
    }

    /**
     * 숨김 상태로 가시성 설정 생성
     */
    public static UserEmotionVisibility createHidden(User user, Emotion emotion) {
        UserEmotionVisibility visibility = new UserEmotionVisibility(user, emotion, false);

        // 연관관계 편의 메서드 호출
        user.addEmotionVisibilitySetting(visibility);
        emotion.addVisibilitySetting(visibility);

        return visibility;
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 감정 숨김
     */
    public void hide() {
        this.isVisible = false;
    }

    /**
     * 감정 표시
     */
    public void show() {
        this.isVisible = true;
    }

    /**
     * 가시성 토글
     */
    public void toggleVisibility() {
        if (!this.emotion.isDefaultEmotion()) {
            throw new IllegalStateException("기본 감정만 가시성을 변경할 수 있습니다.");
        }
        this.isVisible = !this.isVisible;
    }

    // ===== 유효성 검증 메서드 =====
    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }
    }

    private static void validateEmotion(Emotion emotion) {
        if (emotion == null) {
            throw new IllegalArgumentException("감정 정보는 필수입니다.");
        }
    }

    // ===== 조회 편의 메서드 =====
    public boolean isHidden() {
        return !this.isVisible;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public Long getEmotionId() {
        return this.emotion.getId();
    }

    public String getEmotionName() {
        return this.emotion.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserEmotionVisibility that = (UserEmotionVisibility) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}