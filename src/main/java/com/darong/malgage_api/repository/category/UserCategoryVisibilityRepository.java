package com.darong.malgage_api.repository.category;

import com.darong.malgage_api.domain.category.UserCategoryVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCategoryVisibilityRepository extends JpaRepository<UserCategoryVisibility, Long> {
    Optional<UserCategoryVisibility> findByUser_IdAndCategory_Id(Long userId, Long categoryId);
}