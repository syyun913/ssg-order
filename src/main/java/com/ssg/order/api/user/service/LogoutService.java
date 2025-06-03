package com.ssg.order.api.user.service;

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
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(BusinessErrorCode.INVALID_TOKEN, "authorization : " + authorization);
        }

        String token = authorization.substring(7);

        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(BusinessErrorCode.INVALID_TOKEN, "authorization : " + authorization);
        }

        jwtUtil.invalidateToken(token);

        String userId = jwtUtil.getIdFromToken(token);
        cacheStore.delete(JWT_REFRESH_TOKEN_PREFIX + userId);
    }
}
