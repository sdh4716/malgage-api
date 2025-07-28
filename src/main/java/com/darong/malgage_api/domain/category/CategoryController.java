package com.darong.malgage_api.domain.category;

import com.darong.malgage_api.auth.CurrentUser;
import com.darong.malgage_api.auth.exception.CustomAuthException;
import com.darong.malgage_api.domain.category.dto.CategoryRequestDto;
import com.darong.malgage_api.domain.category.dto.CategoryResponseDto;
import com.darong.malgage_api.domain.category.service.CategoryService;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.domain.user.repository.UserRepository;
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
     * ✅ CategoryScope 기준 카테고리 조회
     */
    @GetMapping("/scope/{scope}")
    public ResponseEntity<List<CategoryResponseDto>> getCategoriesByScope(
            @CurrentUser User user,  // 🎉 사용자 권한 확인
            @PathVariable CategoryScope scope
    ) {
        List<CategoryResponseDto> responses = categoryService.getCategoriesByScope(user, scope);
        return ResponseEntity.ok(responses);
    }
}

