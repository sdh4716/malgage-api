package com.darong.malgage_api.domain.emotion.repository;

import com.darong.malgage_api.domain.emotion.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
}