// domain/emotion/Emotion.java
package com.darong.malgage_api.domain.emotion;

import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "idx_emotion_user_scope", columnList = "user_id, scope"),
        @Index(name = "idx_emotion_default_scope", columnList = "scope, sort_order")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emotion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EmotionScope scope;

    /**
     * 감정 아이콘 이름 (Material Design Icons 기준)
     * 예: "sentiment_very_satisfied", "sentiment_dissatisfied", "mood" 등
     */
    @Column(name = "icon_name", length = 50)
    private String iconName;

    /**
     * 커스텀 감정의 소유자 (기본 감정은 null)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 이 감정에 대한 사용자별 가시성 설정들
     */
    @OneToMany(mappedBy = "emotion", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<UserEmotionVisibility> visibilitySettings = new ArrayList<>();

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // ===== private 생성자 =====
    private Emotion(String name, String iconName, Integer sortOrder, EmotionScope scope, User user) {
        validateName(name);
        validateScope(scope);
        validateSortOrder(sortOrder);
        validateIconName(iconName);

        this.name = name.trim();
        this.iconName = iconName != null ? iconName.trim() : null;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.scope = scope;
        this.user = user;
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 기본 감정 생성 - 모든 사용자에게 제공되는 감정 (아이콘 포함)
     */
    public static Emotion createDefault(String name, String iconName, Integer sortOrder) {
        return new Emotion(name, iconName, sortOrder, EmotionScope.DEFAULT, null);
    }

    /**
     * 기본 감정 생성 - 아이콘 없는 버전 (기존 호환성)
     */
    public static Emotion createDefault(String name, Integer sortOrder) {
        return new Emotion(name, null, sortOrder, EmotionScope.DEFAULT, null);
    }

    /**
     * 사용자 커스텀 감정 생성 (아이콘 포함)
     */
    public static Emotion createCustom(String name, String iconName, User user, Integer sortOrder) {
        validateUser(user);
        Emotion emotion = new Emotion(name, iconName, sortOrder, EmotionScope.CUSTOM, user);

        return emotion;
    }

    /**
     * 사용자 커스텀 감정 생성 - 아이콘 없는 버전 (기존 호환성)
     */
    public static Emotion createCustom(String name, User user, Integer sortOrder) {
        return createCustom(name, null, user, sortOrder);
    }

    // ===== 연관관계 편의 메서드 =====

    /**
     * 가시성 설정 추가
     */
    public void addVisibilitySetting(UserEmotionVisibility visibility) {
        if (visibility.getEmotion() == this) {
            this.visibilitySettings.add(visibility);
        }
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 기본 감정인지 확인
     */
    public boolean isDefaultEmotion() {
        return this.scope == EmotionScope.DEFAULT;
    }

    /**
     * 커스텀 감정인지 확인
     */
    public boolean isCustomEmotion() {
        return this.scope == EmotionScope.CUSTOM;
    }

    /**
     * 특정 사용자의 감정인지 확인
     */
    public boolean belongsToUser(Long userId) {
        return this.user != null && this.user.getId().equals(userId);
    }

    /**
     * 아이콘이 설정되어 있는지 확인
     */
    public boolean hasIcon() {
        return this.iconName != null && !this.iconName.trim().isEmpty();
    }

    /**
     * 감정 정보 수정 (커스텀 감정만 수정 가능)
     */
    public void updateEmotion(String name, String iconName, Integer sortOrder) {
        validateCustomEmotion();

        if (name != null && !name.trim().isEmpty()) {
            validateName(name);
            this.name = name.trim();
        }
        if (iconName != null) {
            validateIconName(iconName);
            this.iconName = iconName.trim().isEmpty() ? null : iconName.trim();
        }
        if (sortOrder != null) {
            validateSortOrder(sortOrder);
            this.sortOrder = sortOrder;
        }
    }

    /**
     * 감정 정보 수정 - 아이콘 없는 버전 (기존 호환성)
     */
    public void updateEmotion(String name, Integer sortOrder) {
        updateEmotion(name, this.iconName, sortOrder);
    }

    /**
     * 정렬 순서 변경
     */
    public void updateSortOrder(Integer sortOrder) {
        validateSortOrder(sortOrder);
        this.sortOrder = sortOrder;
    }

    /**
     * 아이콘 변경
     */
    public void updateIcon(String iconName) {
        validateIconName(iconName);
        this.iconName = iconName != null && !iconName.trim().isEmpty() ? iconName.trim() : null;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    // ===== 유효성 검증 메서드 =====

    private void validateCustomEmotion() {
        if (this.scope != EmotionScope.CUSTOM) {
            throw new IllegalStateException("기본 감정은 수정할 수 없습니다.");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("감정명은 필수입니다.");
        }
        if (name.trim().length() > 50) {
            throw new IllegalArgumentException("감정명은 50자를 초과할 수 없습니다.");
        }
    }

    private static void validateScope(EmotionScope scope) {
        if (scope == null) {
            throw new IllegalArgumentException("감정 범위는 필수입니다.");
        }
    }

    private static void validateSortOrder(Integer sortOrder) {
        if (sortOrder != null && sortOrder < 0) {
            throw new IllegalArgumentException("정렬 순서는 0 이상이어야 합니다.");
        }
    }

    private static void validateIconName(String iconName) {
        if (iconName != null && iconName.trim().length() > 50) {
            throw new IllegalArgumentException("아이콘명은 50자를 초과할 수 없습니다.");
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("커스텀 감정은 사용자 정보가 필수입니다.");
        }
    }

    // ===== Object 메서드 오버라이드 =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Emotion emotion = (Emotion) obj;
        return id != null && id.equals(emotion.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Emotion{id=%d, name='%s', scope=%s, iconName='%s'}",
                id, name, scope, iconName);
    }
}