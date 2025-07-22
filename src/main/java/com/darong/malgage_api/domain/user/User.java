package com.darong.malgage_api.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // ▶ 소셜 인증 고유 식별자 (sub)
    @Column(nullable = false, unique = true)
    private String oauthId;

    // ▶ 로그인 제공자: GOOGLE / APPLE / KAKAO 등
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    // ▶ 이메일 (선택 사항, Kakao나 Apple은 비공개 가능성 있음)
    private String email;

    // ▶ 사용자 닉네임 또는 표시 이름
    private String nickname;

    // ▶ 프로필 이미지 (있다면)
    private String profileImageUrl;

    // ✅ 정적 생성 메서드 (실제 서비스에서 사용)
    public static User create(String oauthId, AuthProvider provider, String email, String nickname, String profileImageUrl) {
        User user = new User();
        user.oauthId = oauthId;
        user.provider = provider;
        user.email = email;
        user.nickname = nickname;
        user.profileImageUrl = profileImageUrl;
        return user;
    }

    // ✅ 테스트용 임시 생성자 (소셜 로그인 구현 전까지)
    public User(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.oauthId = "temp-oauth-id";
        this.provider = AuthProvider.GOOGLE;
        this.email = "test@example.com";
        this.profileImageUrl = null;
    }
}
