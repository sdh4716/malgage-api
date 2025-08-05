package com.darong.malgage_api.auth.jwt;

// JWT 필터는 요청당 1회만 실행되어야 하므로 OncePerRequestFilter 상속

import com.darong.malgage_api.auth.util.TokenUtil;
import com.darong.malgage_api.global.exception.NotFoundException;
import com.darong.malgage_api.global.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor // 생성자 주입 자동 생성 (final 필드만 포함)
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT를 검증하고 사용자 정보를 추출하는 컴포넌트
    private final JwtProvider jwtProvider;

    /**
     * auth 관련 요청은 토큰이 만료되었을 경우에도 실행되어야 하기 때문에 filter 예외 처리 
     */
    private static final List<String> EXCLUDE_URLS = List.of(
            "/api/auth/validate",
            "/api/auth/reissue-access-token",
            "/api/auth/google-login",
            "/api/auth/logout"
    );

    private boolean isExcluded(String uri) {
        return EXCLUDE_URLS.stream().anyMatch(uri::startsWith);
    }

    /**
     * 매 요청 시마다 실행되는 필터 메서드
     * JWT를 꺼내고 유효하면 SecurityContext에 인증 정보 저장
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 인증 예외 처리할 URI 제외
        if (isExcluded(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = TokenUtil.resolveToken(request);

        try {
            if (token == null) {
                throw new UnauthorizedException("AccessToken이 누락되었습니다.");
            }

            if (!jwtProvider.validateToken(token)) {
                throw new UnauthorizedException("AccessToken이 유효하지 않습니다.");
            }

            SecurityContextHolder.getContext().setAuthentication(jwtProvider.getAuthentication(token));
        } catch (JwtException | UnauthorizedException e) {
            log.warn("❗ JWT 인증 실패: {}", e.getMessage());
            throw e; // ✅ 전역 @RestControllerAdvice에서 처리
        }

        filterChain.doFilter(request, response);
    }


}
