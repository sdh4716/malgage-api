package com.darong.malgage_api.repository.emotion;

import com.darong.malgage_api.domain.emotion.UserEmotionVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEmotionVisibilityRepository extends JpaRepository<UserEmotionVisibility, Long> {
    Optional<UserEmotionVisibility> findByUser_IdAndEmotion_Id(Long userId, Long emotionId);
}