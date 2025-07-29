// domain/category/service/CategoryService.java
package com.darong.malgage_api.service;

import com.darong.malgage_api.auth.exception.CustomAuthException;
import com.darong.malgage_api.controller.dto.request.category.CategoryRequestDto;
import com.darong.malgage_api.controller.dto.request.category.CategoryVisibilityRequestDto;
import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.CategoryScope;
import com.darong.malgage_api.controller.dto.response.CategoryResponseDto;
import com.darong.malgage_api.domain.category.UserCategoryVisibility;
import com.darong.malgage_api.global.exception.NotFoundException;
import com.darong.malgage_api.repository.category.CategoryQueryRepository;
import com.darong.malgage_api.repository.category.CategoryRepository;
import com.darong.malgage_api.repository.category.UserCategoryVisibilityRepository;
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

    /**
     * 카테고리 등록
     */
    @Transactional
    public CategoryResponseDto createCustomCategory(User user, CategoryRequestDto dto) {
        Category category = Category.createCustom(
                dto.getName(),
                dto.getType(),
                dto.getIconName(),
                user,
                dto.getSortOrder()
        );

        Category saved = categoryRepository.save(category);
        return CategoryResponseDto.from(saved);
    }

    /**
     * 카테고리 가시성 설정
     */
    @Transactional
    public void updateVisibility(User user, CategoryVisibilityRequestDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다."));

        // 본인이 소유하거나 기본 카테고리인 경우만 허용
        if (category.isCustomCategory() && !category.belongsToUser(user.getId())) {
            throw new CustomAuthException("해당 카테고리에 대한 권한이 없습니다.");
        }

        UserCategoryVisibility visibility = visibilityRepository.findByUser_IdAndCategory_Id(user.getId(), dto.getCategoryId())
                .orElseGet(() -> UserCategoryVisibility.createVisible(user, category)); // 없으면 생성

        if (dto.getVisible() != null) {
            if (dto.getVisible()) {
                visibility.show();
            } else {
                visibility.hide();
            }
        }

        visibilityRepository.save(visibility);
    }



}