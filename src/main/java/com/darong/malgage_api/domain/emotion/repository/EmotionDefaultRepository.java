package com.darong.malgage_api.domain.emotion.repository;

import com.darong.malgage_api.domain.emotion.EmotionDefault;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionDefaultRepository extends JpaRepository<EmotionDefault, Long> {

    boolean existsByName(String name);
}
