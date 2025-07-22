package com.darong.malgage_api.domain.emotion;

import com.darong.malgage_api.domain.category.CategoryDefault;
import com.darong.malgage_api.domain.category.CategoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionDefault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_default_id")
    private Long id;

    private String name;

    private int sortOrder;

    // ✅ 생성 메서드
    public static EmotionDefault create(String name, int sortOrder) {
        EmotionDefault emotion = new EmotionDefault();
        emotion.name = name;
        emotion.sortOrder = sortOrder;
        return emotion;
    }
}

