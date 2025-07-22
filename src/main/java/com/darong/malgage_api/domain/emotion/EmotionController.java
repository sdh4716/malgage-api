package com.darong.malgage_api.domain.emotion;

import com.darong.malgage_api.domain.emotion.dto.EmotionResponseDto;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @GetMapping
    public ResponseEntity<List<EmotionResponseDto>> getEmotions(@AuthenticationPrincipal User user) {
        List<EmotionResponseDto> result = emotionService.getEmotionsForUser(user);
        return ResponseEntity.ok(result);
    }
}
