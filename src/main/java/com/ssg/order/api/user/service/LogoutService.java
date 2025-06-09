package com.ssg.order.api.user.service;

import static com.ssg.order.domain.common.annotation.constants.CommonConstants.JWT_REFRESH_TOKEN_PREFIX;

import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.token.TokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * 로그아웃 처리가 수행됩니다.
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenHandler tokenHandler;

    private final CacheStore cacheStore;

    /**
     * 로그아웃 요청을 처리하고, 액세스 토큰과 리프레시 토큰을 무효화한다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(BusinessErrorCode.INVALID_TOKEN, "authorization : " + authorization);
        }

        String token = authorization.substring(7);

        if (!tokenHandler.validateToken(token)) {
            throw new BusinessException(BusinessErrorCode.INVALID_TOKEN, "authorization : " + authorization);
        }

        tokenHandler.invalidateToken(token);

        String userId = tokenHandler.getIdFromToken(token);
        cacheStore.delete(JWT_REFRESH_TOKEN_PREFIX + userId);
    }
}
