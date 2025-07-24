package com.darong.malgage_api.auth.util;

import jakarta.servlet.http.HttpServletRequest;

public class TokenUtil {

    /**
     * HTTP 요청의 Authorization 헤더에서 JWT 토큰을 추출하는 메서드
     * 형식: "Bearer <token>"
     */
    public static String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
