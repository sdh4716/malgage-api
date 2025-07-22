package com.darong.malgage_api.domain.emotion;

import com.darong.malgage_api.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_emotion_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_emotion_id")
    private EmotionDefault baseEmotion;

    @Column(nullable = false)
    private boolean enabled = true;

    public static UserEmotion create(String name, User user, EmotionDefault baseEmotion) {
        UserEmotion emotion = new UserEmotion();
        emotion.name = name;
        emotion.user = user;
        emotion.baseEmotion = baseEmotion;
        emotion.enabled = true;
        return emotion;
    }

    public void disable() {
        this.enabled = false;
    }
}
