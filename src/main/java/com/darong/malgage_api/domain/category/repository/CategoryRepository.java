// domain/category/repository/CategoryRepository.java
package com.darong.malgage_api.domain.category.repository;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.CategoryScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 기본 카테고리만 조회 (타입별, 정렬순서로 정렬)
     */
    List<Category> findByScopeOrderByTypeAscSortOrderAsc(CategoryScope scope);

    /**
     * 사용자의 커스텀 카테고리만 조회 (타입별, 정렬순서로 정렬)
     */
    List<Category> findByUserIdAndScopeOrderByTypeAscSortOrderAsc(Long userId, CategoryScope scope);
}