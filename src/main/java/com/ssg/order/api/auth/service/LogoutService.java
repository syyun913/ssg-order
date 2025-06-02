package com.ssg.order.api.auth.service;

import static com.ssg.order.domain.common.annotation.constants.CommonConstants.JWT_REFRESH_TOKEN_PREFIX;

import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.infra.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final JwtUtil jwtUtil;

    private final CacheStore cacheStore;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        // Authorization 헤더에서 토큰 추출
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(BusinessErrorCode.INVALID_TOKEN, "authorization : " + authorization);
        }

        String token = authorization.substring(7);

        // 토큰 유효성 검증
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(BusinessErrorCode.INVALID_TOKEN, "authorization : " + authorization);
        }

        // 토큰을 블랙리스트에 추가
        jwtUtil.invalidateToken(token);

        // 사용자 ID 추출하여 Refresh Token도 삭제
        String userId = jwtUtil.getIdFromToken(token);
        cacheStore.delete(JWT_REFRESH_TOKEN_PREFIX + userId);
    }
}
