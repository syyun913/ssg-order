package com.ssg.order.api.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.token.TokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@DisplayName("로그아웃 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenHandler tokenHandler;

    @Mock
    private CacheStore cacheStore;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutService logoutService;

    @Nested
    @DisplayName("로그아웃 처리")
    class Logout {
        @Test
        @DisplayName("정상적인 로그아웃 요청 시 토큰이 무효화되고 리프레시 토큰이 삭제된다")
        void logout_ShouldSucceed() {
            // given
            String token = "valid.token.here";
            String userId = "testUser";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(tokenHandler.validateToken(token)).thenReturn(true);
            when(tokenHandler.getIdFromToken(token)).thenReturn(userId);

            // when
            logoutService.logout(request, response, authentication);

            // then
            verify(tokenHandler).invalidateToken(token);
            verify(cacheStore).delete("jwt_refresh-token:" + userId);
        }

        @Test
        @DisplayName("Authorization 헤더가 없는 경우 BusinessException이 발생한다")
        void logout_ShouldThrowException_WhenNoAuthorizationHeader() {
            // given
            when(request.getHeader("Authorization")).thenReturn(null);

            // when & then
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> logoutService.logout(request, response, authentication)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_TOKEN);
        }

        @Test
        @DisplayName("Bearer 토큰 형식이 아닌 경우 BusinessException이 발생한다")
        void logout_ShouldThrowException_WhenInvalidTokenFormat() {
            // given
            when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

            // when & then
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> logoutService.logout(request, response, authentication)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_TOKEN);
        }

        @Test
        @DisplayName("유효하지 않은 토큰인 경우 BusinessException이 발생한다")
        void logout_ShouldThrowException_WhenInvalidToken() {
            // given
            String token = "invalid.token.here";
            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(tokenHandler.validateToken(token)).thenReturn(false);

            // when & then
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> logoutService.logout(request, response, authentication)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_TOKEN);
        }
    }
}