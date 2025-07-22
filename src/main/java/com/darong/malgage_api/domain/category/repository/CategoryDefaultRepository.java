package com.darong.malgage_api.domain.category.repository;

import com.darong.malgage_api.domain.category.CategoryDefault;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDefaultRepository extends JpaRepository<CategoryDefault, Long> {

    boolean existsByName(String name);
}
