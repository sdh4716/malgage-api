package com.darong.malgage_api.controller;

import com.darong.malgage_api.global.security.CurrentUser;
import com.darong.malgage_api.controller.dto.request.category.CategorySaveRequestDto;
import com.darong.malgage_api.controller.dto.request.category.CategoryVisibilityRequestDto;
import com.darong.malgage_api.domain.category.CategoryScope;
import com.darong.malgage_api.controller.dto.response.category.CategoryResponseDto;
import com.darong.malgage_api.domain.category.CategoryType;
import com.darong.malgage_api.service.CategoryService;
import com.darong.malgage_api.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 카테고리 컨트롤러
 * 전체 조회, CategoryScope 기준 조회만 제공
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * ✅ 사용자의 모든 카테고리 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(
            @CurrentUser User user  // 🎉 해당 사용자의 카테고리만 조회
    ) {
        List<CategoryResponseDto> responses = categoryService.getAllCategories(user);
        return ResponseEntity.ok(responses);
    }

    /**
     * ✅ CategoryScope 및 CategoryType 기준 카테고리 조회
     * @param user 현재 로그인 된 사용자
     * @param scope (default/custom)
     * @param type 카테고리 타입 (income/expense)
     */
    @GetMapping("/scope/{scope}")
    public ResponseEntity<List<CategoryResponseDto>> getCategoriesByScopeAndType(
            @CurrentUser User user,
            @PathVariable CategoryScope scope,
            @RequestParam(required = false) CategoryType type  // 👈 쿼리 파라미터로 type 추가
    ) {
        List<CategoryResponseDto> responses = categoryService.getCategoriesByScope(user, scope, type);
        return ResponseEntity.ok(responses);
    }

    /**
     * ✅ 사용자가 visible=true로 설정한 카테고리 목록 조회
     * @param user 현재 로그인 된 사용자
     * @param type 카테고리 타입 (income/expense)
     */
    @GetMapping("/visible")
    public ResponseEntity<List<CategoryResponseDto>> getVisibleCategories(
            @CurrentUser User user,
            @RequestParam String type
    ) {
        CategoryType categoryType = CategoryType.valueOf(type.toUpperCase()); // 문자열을 enum으로 변환
        List<CategoryResponseDto> categories = categoryService.getVisibleCategories(user, categoryType);
        return ResponseEntity.ok(categories);
    }

    /**
     * ✅ CustomCategory 생성
     * @param user 현재 로그인 된 사용자
     * @param dto 카테고리 저장 전용 request dto
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCustomCategory(
            @CurrentUser User user,
            @Valid @RequestBody CategorySaveRequestDto dto
    ) {
        CategoryResponseDto response = categoryService.createCustomCategory(user, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 카테고리 가시성 수정
     * @param user 현재 로그인 된 사용자
     * @param dto 카테고리 가시성 수정 전용 request dto
     */
    @PatchMapping("/visibility")
    public ResponseEntity<Void> updateVisibility(
            @CurrentUser User user,
            @RequestBody CategoryVisibilityRequestDto dto
    ) {
        categoryService.updateVisibility(user, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 커스텀 카테고리 삭제
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCustomCategory(
            @CurrentUser User user,
            @PathVariable Long categoryId
    ) {
        categoryService.softDeleteCategory(user, categoryId);
        return ResponseEntity.noContent().build();
    }
}

