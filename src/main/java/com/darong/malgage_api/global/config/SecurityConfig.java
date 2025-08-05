package com.darong.malgage_api.global.config;

import com.darong.malgage_api.auth.jwt.JwtAuthenticationFilter;
import com.darong.malgage_api.auth.jwt.JwtProvider;
import com.darong.malgage_api.global.security.CustomAccessDeniedHandler;
import com.darong.malgage_api.global.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration // 이 클래스가 스프링 설정 클래스임을 명시
@EnableWebSecurity // 스프링 시큐리티 활성화
@RequiredArgsConstructor // 생성자 주입 자동 생성 (final 필드만 포함)
public class SecurityConfig {

    // JWT 관련 기능 제공하는 컴포넌트 (토큰 생성, 검증 등)
    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint; //

    /**
     * ✅ JwtAuthenticationFilter 빈 등록
     * - 매 요청마다 JWT 토큰 유무 및 유효성 검사를 수행
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider);
    }

    /**
     * ✅ SecurityFilterChain 설정
     * - Spring Security 설정의 핵심 구성
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 비활성화 (JWT는 세션을 사용하지 않기 때문에 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ 세션을 아예 사용하지 않도록 설정 (JWT 기반 인증을 위한 필수 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 요청 URL에 따른 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 로그인, 회원가입 등 인증 필요 없는 엔드포인트 허용
                        .anyRequest().authenticated() // 나머지 모든 요청은 인증 필요
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // ✅ 꼭 등록
                )

                // ✅ JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // ✅ 최종 Security 설정 객체 반환
        return http.build();
    }
}
