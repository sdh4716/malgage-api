// global/config/UserArgumentResolver.java
package com.darong.malgage_api.global.config;

import com.darong.malgage_api.auth.CurrentUser;
import com.darong.malgage_api.auth.security.UserPrincipal;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    /**
     * 이 Resolver가 처리할 파라미터인지 확인
     * - @CurrentUser 어노테이션이 있고
     * - 파라미터 타입이 User인 경우
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * 실제 User 객체를 반환
     * - SecurityContext에서 UserPrincipal 추출
     * - UserRepository로 User 조회
     * - 없으면 예외 발생
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.debug("authentication = " + String.valueOf(authentication.isAuthenticated()));

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다.");
        }

        // UserPrincipal에서 userId 추출
        Long userId = extractUserIdFromAuthentication(authentication);

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
    }

    /**
     * Authentication에서 userId 추출
     * 현재 UserPrincipal 구조에 맞게 수정
     */
    private Long extractUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        // UserPrincipal에서 userId 추출
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUserId();
        }

        // UserPrincipal이 아닌 경우 (예: 익명 사용자, 다른 Principal 타입)
        throw new IllegalArgumentException("UserPrincipal을 찾을 수 없습니다. principal: " + principal.getClass().getSimpleName());
    }
}