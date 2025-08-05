// domain/user/User.java (감정 연관관계 추가된 버전)
package com.darong.malgage_api.domain.user;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.UserCategoryVisibility;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.UserEmotionVisibility;
import com.darong.malgage_api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * 소셜 인증 고유 식별자 (sub)
     */
    @Column(nullable = false, unique = true, length = 100)
    private String oauthId;

    /**
     * 로그인 제공자: GOOGLE / APPLE / KAKAO 등
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    /**
     * 이메일 (선택 사항, Kakao나 Apple은 비공개 가능성 있음)
     */
    @Column(length = 100)
    private String email;

    /**
     * 사용자 닉네임 또는 표시 이름
     */
    @Column(length = 50)
    private String nickname;

    /**
     * 프로필 이미지 URL (있다면)
     */
    @Column(length = 500)
    private String profileImageUrl;

    // ===== 카테고리 연관관계 매핑 =====

    /**
     * 사용자가 생성한 커스텀 카테고리들
     */
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Category> customCategories = new ArrayList<>();

    /**
     * 사용자의 카테고리 가시성 설정들
     */
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<UserCategoryVisibility> categoryVisibilitySettings = new ArrayList<>();

    // ===== 감정 연관관계 매핑 =====

    /**
     * 사용자가 생성한 커스텀 감정들
     */
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Emotion> customEmotions = new ArrayList<>();

    /**
     * 사용자의 감정 가시성 설정들
     */
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<UserEmotionVisibility> emotionVisibilitySettings = new ArrayList<>();

    // ===== 정적 생성 메서드 =====

    /**
     * 실제 서비스에서 사용할 소셜 로그인 사용자 생성
     */
    public static User create(String oauthId, AuthProvider provider, String email, String nickname, String profileImageUrl) {
        User user = new User();
        user.oauthId = oauthId;
        user.provider = provider;
        user.email = email;
        user.nickname = nickname;
        user.profileImageUrl = profileImageUrl;
        return user;
    }

    /**
     * 테스트용 임시 사용자 생성 (소셜 로그인 구현 전까지)
     */
    public static User createForTest(String nickname) {
        User user = new User();
        user.oauthId = "temp-oauth-" + System.currentTimeMillis();
        user.provider = AuthProvider.GOOGLE;
        user.email = "test@example.com";
        user.nickname = nickname;
        user.profileImageUrl = null;
        return user;
    }

    /**
     * 이메일과 닉네임으로 간단 생성 (개발/테스트용)
     */
    public static User createSimple(String email, String nickname) {
        User user = new User();
        user.oauthId = "simple-" + email;
        user.provider = AuthProvider.GOOGLE;
        user.email = email;
        user.nickname = nickname;
        user.profileImageUrl = null;
        return user;
    }

    // ===== 카테고리 연관관계 편의 메서드 =====

    /**
     * 커스텀 카테고리 추가
     */
    public void addCustomCategory(Category category) {
        if (category.isCustomCategory() && category.getUser() == this) {
            this.customCategories.add(category);
        }
    }

    /**
     * 카테고리 가시성 설정 추가
     */
    public void addCategoryVisibilitySetting(UserCategoryVisibility visibility) {
        if (visibility.getUser() == this) {
            this.categoryVisibilitySettings.add(visibility);
        }
    }

    // ===== 감정 연관관계 편의 메서드 =====

    /**
     * 커스텀 감정 추가
     */
    public void addCustomEmotion(Emotion emotion) {
        if (emotion.isCustomEmotion() && emotion.getUser() == this) {
            this.customEmotions.add(emotion);
        }
    }

    /**
     * 감정 가시성 설정 추가
     */
    public void addEmotionVisibilitySetting(UserEmotionVisibility visibility) {
        if (visibility.getUser() == this) {
            this.emotionVisibilitySettings.add(visibility);
        }
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 프로필 정보 업데이트
     */
    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null && !nickname.trim().isEmpty()) {
            this.nickname = nickname.trim();
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    /**
     * 이메일 업데이트 (소셜 로그인에서 추가 정보 제공 시)
     */
    public void updateEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            this.email = email.trim();
        }
    }

    /**
     * 표시용 이름 반환 (닉네임 우선, 없으면 이메일)
     */
    public String getDisplayName() {
        if (nickname != null && !nickname.trim().isEmpty()) {
            return nickname;
        }
        if (email != null && !email.trim().isEmpty()) {
            return email;
        }
        return "사용자" + id;
    }

    /**
     * 프로필 이미지가 있는지 확인
     */
    public boolean hasProfileImage() {
        return profileImageUrl != null && !profileImageUrl.trim().isEmpty();
    }

    // ===== Object 메서드 오버라이드 =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, nickname='%s', provider=%s}",
                id, nickname, provider);
    }
}