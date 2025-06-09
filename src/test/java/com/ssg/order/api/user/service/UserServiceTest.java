package com.ssg.order.api.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssg.order.api.user.mapper.UserDtoMapper;
import com.ssg.order.api.user.service.request.LoginRequest;
import com.ssg.order.api.user.service.request.RegisterRequest;
import com.ssg.order.api.user.service.response.LoginResponse;
import com.ssg.order.api.user.service.response.ReissueResponse;
import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.domain.user.User;
import com.ssg.order.domain.domain.user.usecase.UserUseCase;
import com.ssg.order.domain.token.TokenHandler;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("사용자 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private CacheStore cacheStore;

    @Mock
    private TokenHandler tokenHandler;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "accessExpirationMinutes", 30L);
        ReflectionTestUtils.setField(userService, "refreshExpirationDays", 7L);
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("유효한 로그인 요청 시 액세스 토큰과 리프레시 토큰이 발급된다")
        void login_ShouldSucceed() {
            // given
            String username = "testUser";
            String password = "password";
            String accessToken = "access.token";
            String refreshToken = "refresh.token";
            
            LoginRequest loginRequest = mock(LoginRequest.class);
            when(loginRequest.getUserName()).thenReturn(username);
            when(loginRequest.getPassword()).thenReturn(password);

            User user = User.builder()
                .userName(username)
                .build();

            when(userUseCase.getUserByUserName(username)).thenReturn(user);
            when(tokenHandler.generateToken(username, 30L * 60 * 1000L)).thenReturn(accessToken);
            when(tokenHandler.generateToken(username, 7L * 24 * 60 * 60 * 1000L)).thenReturn(refreshToken);

            // when
            LoginResponse response = userService.login(loginRequest);

            // then
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(cacheStore).put(eq("jwt_refresh-token:" + username), eq(refreshToken), any(Duration.class));
            assertThat(response.getAccessToken()).isEqualTo(accessToken);
            assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        }
    }

    @Nested
    @DisplayName("회원가입")
    class Register {
        @Test
        @DisplayName("새로운 사용자로 회원가입 시 성공적으로 등록된다")
        void register_ShouldSucceed() {
            // given
            String username = "newUser";
            String password = "password";
            String encodedPassword = "encodedPassword";

            RegisterRequest registerRequest = mock(RegisterRequest.class);
            when(registerRequest.getUserName()).thenReturn(username);
            when(registerRequest.getPassword()).thenReturn(password);

            User user = User.builder()
                .userName(username)
                .password(encodedPassword)
                .build();

            when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
            when(userUseCase.isUserExists(username)).thenReturn(false);
            when(userDtoMapper.toDomain(registerRequest)).thenReturn(user);

            // when
            userService.register(registerRequest);

            // then
            verify(userUseCase).saveUser(user);
        }

        @Test
        @DisplayName("이미 존재하는 사용자로 회원가입 시도 시 BusinessException이 발생한다")
        void register_ShouldThrowException_WhenUserExists() {
            // given
            String username = "existingUser";
            RegisterRequest registerRequest = mock(RegisterRequest.class);
            when(registerRequest.getUserName()).thenReturn(username);
            when(registerRequest.getPassword()).thenReturn("password");

            when(userUseCase.isUserExists(username)).thenReturn(true);

            // when & then
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.register(registerRequest)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.EXIST_USER);
        }
    }

    @Nested
    @DisplayName("토큰 재발급")
    class Reissue {
        @Test
        @DisplayName("유효한 리프레시 토큰으로 재발급 요청 시 새로운 액세스 토큰이 발급된다")
        void reissue_ShouldSucceed() {
            // given
            String refreshToken = "valid.refresh.token";
            String userId = "testUser";
            String newAccessToken = "new.access.token";
            String savedRefreshToken = refreshToken;

            when(tokenHandler.getIdFromToken(refreshToken)).thenReturn(userId);
            when(cacheStore.get("jwt_refresh-token:" + userId, String.class)).thenReturn(savedRefreshToken);
            when(tokenHandler.generateToken(userId, 30L * 60 * 1000L)).thenReturn(newAccessToken);

            // when
            ReissueResponse response = userService.reissue(refreshToken);

            // then
            assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        }

        @Test
        @DisplayName("저장된 리프레시 토큰과 일치하지 않는 경우 BusinessException이 발생한다")
        void reissue_ShouldThrowException_WhenRefreshTokenMismatch() {
            // given
            String refreshToken = "valid.refresh.token";
            String userId = "testUser";
            String savedRefreshToken = "different.refresh.token";

            when(tokenHandler.getIdFromToken(refreshToken)).thenReturn(userId);
            when(cacheStore.get("jwt_refresh-token:" + userId, String.class)).thenReturn(savedRefreshToken);

            // when & then
            BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.reissue(refreshToken)
            );

            // then
            assertThat(exception.getErrorCode()).isEqualTo(BusinessErrorCode.INVALID_TOKEN);
        }
    }
}