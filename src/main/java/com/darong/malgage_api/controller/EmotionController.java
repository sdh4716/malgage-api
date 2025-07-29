package com.darong.malgage_api.controller;

import com.darong.malgage_api.auth.CurrentUser;
import com.darong.malgage_api.domain.emotion.EmotionScope;
import com.darong.malgage_api.controller.dto.response.EmotionResponseDto;
import com.darong.malgage_api.service.EmotionService;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
