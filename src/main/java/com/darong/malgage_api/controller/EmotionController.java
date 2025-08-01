package com.darong.malgage_api.controller;

import com.darong.malgage_api.auth.CurrentUser;
import com.darong.malgage_api.controller.dto.request.category.CategoryVisibilityRequestDto;
import com.darong.malgage_api.controller.dto.request.emotion.EmotionRequestDto;
import com.darong.malgage_api.controller.dto.request.emotion.EmotionVisibilityRequestDto;
import com.darong.malgage_api.controller.dto.response.category.CategoryResponseDto;
import com.darong.malgage_api.domain.category.CategoryType;
import com.darong.malgage_api.domain.emotion.EmotionScope;
import com.darong.malgage_api.controller.dto.response.emotion.EmotionResponseDto;
import com.darong.malgage_api.service.EmotionService;
import com.darong.malgage_api.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    /**
     * ✅ 사용자의 모든 감정 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<EmotionResponseDto>> getAllEmotions(
            @CurrentUser User user  // 🎉 해당 사용자의 감정 조회
    ) {
        List<EmotionResponseDto> responses = emotionService.getAllEmotions(user);
        return ResponseEntity.ok(responses);
    }

    /**
     * ✅ CategoryScope 기준 카테고리 조회
     */
    @GetMapping("/scope/{scope}")
    public ResponseEntity<List<EmotionResponseDto>> getCategoriesByScope(
            @CurrentUser User user,  // 🎉 사용자 권한 확인
            @PathVariable EmotionScope scope
    ) {
        List<EmotionResponseDto> responses = emotionService.getEmotionsByScope(user, scope);
        return ResponseEntity.ok(responses);
    }

    /**
     * ✅ 사용자가 visible=true로 설정한 감정 목록 조회
     * @param user 현재 로그인 된 사용자
     */
    @GetMapping("/visible")
    public ResponseEntity<List<EmotionResponseDto>> getVisibleCategories(
            @CurrentUser User user
    ) {
        // 문자열을 enum으로 변환
        List<EmotionResponseDto> categories = emotionService.getVisibleEmotions(user);
        return ResponseEntity.ok(categories);
    }

    /**
     * ✅ CustomEmotion 생성
     */
    @PostMapping
    public ResponseEntity<EmotionResponseDto> createCustomEmotion(
            @CurrentUser User user,
            @Valid @RequestBody EmotionRequestDto dto
    ) {
        EmotionResponseDto response = emotionService.createCustomEmotion(user, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 감정 가시성 수정
     */
    @PatchMapping("/visibility")
    public ResponseEntity<Void> updateVisibility(
            @CurrentUser User user,
            @RequestBody EmotionVisibilityRequestDto dto
    ) {
        emotionService.updateVisibility(user, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ CustomEmotion 삭제
     */
    @DeleteMapping("/{emotionId}")
    public ResponseEntity<Void> deleteCustomEmotion(
            @CurrentUser User user,
            @PathVariable Long emotionId
    ) {
        emotionService.deleteCustomEmotion(user, emotionId);
        return ResponseEntity.noContent().build();
    }
}
