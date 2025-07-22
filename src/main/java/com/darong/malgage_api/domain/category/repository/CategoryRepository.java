package com.darong.malgage_api.domain.category.repository;

import com.darong.malgage_api.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}