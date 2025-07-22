package com.darong.malgage_api.domain.category.repository;

import com.darong.malgage_api.domain.category.UserCategory;
import com.darong.malgage_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {

    List<UserCategory> findByUserAndEnabledTrue(User user);

    boolean existsByNameAndUser(String name, User user);
}
