package com.darong.malgage_api.domain.emotion.repository;

import com.darong.malgage_api.domain.emotion.UserEmotion;
import com.darong.malgage_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserEmotionRepository extends JpaRepository<UserEmotion, Long> {

    List<UserEmotion> findByUserAndEnabledTrue(User user);

    boolean existsByNameAndUser(String name, User user);
}
