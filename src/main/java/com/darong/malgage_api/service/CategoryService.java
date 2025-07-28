// domain/category/service/CategoryService.java
package com.darong.malgage_api.domain.category.service;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.CategoryScope;
import com.darong.malgage_api.domain.category.CategoryType;
import com.darong.malgage_api.domain.category.UserCategoryVisibility;
import com.darong.malgage_api.domain.category.dto.CategoryRequestDto;
import com.darong.malgage_api.domain.category.dto.CategoryResponseDto;
import com.darong.malgage_api.domain.category.repository.CategoryQueryRepository;
import com.darong.malgage_api.domain.category.repository.CategoryRepository;
import com.darong.malgage_api.domain.category.repository.UserCategoryVisibilityRepository;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 서비스
 * Spring Data JPA의 기본 메서드만 사용
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserCategoryVisibilityRepository visibilityRepository;
    private final CategoryQueryRepository categoryQueryRepository;

    /**
     * 사용자의 모든 카테고리 조회 (기본 + 커스텀)
     * DB에서 필요한 데이터만 조회하여 성능 최적화
     */
    public List<CategoryResponseDto> getAllCategories(User user) {
        List<Category> categories = categoryQueryRepository.findAllCategoriesForUser(user.getId());

        return categories.stream()
                .map(CategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * CategoryScope 기준 카테고리 조회
     * 스코프에 따라 적절한 Repository 메서드 선택
     */
    public List<CategoryResponseDto> getCategoriesByScope(User user, CategoryScope scope) {
        List<Category> categories;

        if (scope == CategoryScope.DEFAULT) {
            // 기본 카테고리 조회 - 모든 사용자가 볼 수 있음
            categories = categoryRepository.findByScopeOrderByTypeAscSortOrderAsc(scope);
        } else {
            // 커스텀 카테고리 조회 - 해당 사용자만 볼 수 있음
            categories = categoryRepository.findByUserIdAndScopeOrderByTypeAscSortOrderAsc(user.getId(), scope);
        }

        return categories.stream()
                .map(CategoryResponseDto::from)
                .collect(Collectors.toList());
    }

}