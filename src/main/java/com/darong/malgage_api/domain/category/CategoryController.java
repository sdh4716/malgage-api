package com.darong.malgage_api.domain.category;

import com.darong.malgage_api.domain.category.dto.CategoryResponseDto;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategories(@AuthenticationPrincipal User user) {
        List<CategoryResponseDto> result = categoryService.getCategoriesForUser(user);
        return ResponseEntity.ok(result);
    }
}

