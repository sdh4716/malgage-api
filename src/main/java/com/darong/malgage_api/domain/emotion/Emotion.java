package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emotion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Emotion create(String name, User user) {
        Emotion emotion = new Emotion();
        emotion.name = name;
        emotion.user = user;
        return emotion;
    }
}
