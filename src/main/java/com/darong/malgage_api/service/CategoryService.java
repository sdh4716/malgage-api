// domain/category/service/CategoryService.java
package com.darong.malgage_api.service;

import com.darong.malgage_api.controller.dto.request.category.CategorySaveRequestDto;
import com.darong.malgage_api.controller.dto.request.category.CategoryVisibilityRequestDto;
import com.darong.malgage_api.domain.category.*;
import com.darong.malgage_api.controller.dto.response.category.CategoryResponseDto;
import com.darong.malgage_api.global.exception.NotFoundException;
import com.darong.malgage_api.repository.category.CategoryQueryRepository;
import com.darong.malgage_api.repository.category.CategoryRepository;
import com.darong.malgage_api.repository.category.UserCategoryVisibilityRepository;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    public List<CategoryResponseDto> getCategoriesByScope(User user, CategoryScope scope, CategoryType type) {
        if (scope == CategoryScope.DEFAULT) {
            return categoryQueryRepository.findDefaultCategoriesWithVisibility(user, type);
        } else {
            return categoryQueryRepository.findCustomCategoriesWithVisibility(user, type);
        }
    }

    public List<CategoryResponseDto> getVisibleCategories(User user, CategoryType type) {
        return categoryQueryRepository.findVisibleCategoriesByUserAndType(user, type);
    }

    /**
     * 카테고리 등록
     */
    @Transactional
    public CategoryResponseDto createCustomCategory(User user, CategorySaveRequestDto dto) {
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
            throw new AccessDeniedException("해당 카테고리에 대한 권한이 없습니다.");
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

    /**
     * Custom 카테고리 삭제
     * 사용자가 record에서 해당 카테고리를 사용했을 수도 있기 때문에
     * 실제 삭제가 아닌 soft delete
     */
    @Transactional
    public void softDeleteCategory(User user, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인의 카테고리만 삭제할 수 있습니다.");
        }

        category.markAsDeleted();  // isDeleted = true 로 변경
    }


}