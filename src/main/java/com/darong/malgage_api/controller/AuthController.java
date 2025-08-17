package com.darong.malgage_api.controller;

import com.darong.malgage_api.controller.dto.request.auth.AppleLoginRequest;
import com.darong.malgage_api.controller.dto.request.auth.GoogleLoginRequest;
import com.darong.malgage_api.controller.dto.response.auth.TokenResponse;
import com.darong.malgage_api.global.jwt.JwtProvider;
import com.darong.malgage_api.service.auth.AppleLoginService;
import com.darong.malgage_api.service.auth.AuthService;
import com.darong.malgage_api.service.auth.GoogleLoginService;
import com.darong.malgage_api.global.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GoogleLoginService googleLoginService;
    private final AppleLoginService appleLoginService;

    private final JwtProvider jwtProvider;
    private final AuthService authService;

    @PostMapping("/google-login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        TokenResponse tokenResponse = googleLoginService.login(request.getIdToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/apple-login")
    public ResponseEntity<?> loginWithApple(@RequestBody AppleLoginRequest request) {
        TokenResponse tokenResponse = appleLoginService.login(request.getIdToken());
        return ResponseEntity.ok(tokenResponse);
    }

    // ✅ AccessToken 유효성 검사 API
    @GetMapping("/validate")
    public ResponseEntity<?> validateAccessToken(HttpServletRequest request) {

        String accessToken = TokenUtil.resolveToken(request);
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰이 없습니다.");
        }

        try {
            jwtProvider.validateToken(accessToken); // 예외 발생 시 catch로
            String email = jwtProvider.extractEmail(accessToken);
            return ResponseEntity.ok("유효한 토큰입니다. (email: " + email + ")");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ✅ AccessToken, RefreshToken 재발급 API
    @PostMapping("/reissue-access-token")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request) {
        System.out.println("reissue 탐");
        String refreshToken = TokenUtil.resolveToken(request);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("RefreshToken이 없습니다.");
        }

        try {
            TokenResponse response = authService.reissueAccessToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // ✅ 로그아웃 API (DB에 저장된 Refresh Token 삭제)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Authorization 헤더가 잘못되었습니다.");
        }

        String refreshToken = authHeader.substring(7);
        authService.logoutByRefreshToken(refreshToken);
        return ResponseEntity.ok("로그아웃 완료");
    }
}
