package com.ssg.order.api.user.service;

import static com.ssg.order.domain.common.annotation.constants.CommonConstants.JWT_REFRESH_TOKEN_PREFIX;

import com.ssg.order.api.user.service.response.LoginResponse;
import com.ssg.order.api.user.mapper.UserDtoMapper;
import com.ssg.order.api.user.service.request.LoginRequest;
import com.ssg.order.api.user.service.request.RegisterRequest;
import com.ssg.order.api.user.service.response.ReissueResponse;
import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.token.TokenHandler;
import com.ssg.order.domain.domain.user.User;
import com.ssg.order.domain.domain.user.usecase.UserUseCase;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * 사용자 도메인과 관련된 함수들이 정의됩니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;
    private final UserUseCase userUseCase;
    private final CacheStore cacheStore;
    private final TokenHandler tokenHandler;

    @Value("${security.jwt.access-expiration-minutes}")
    private long accessExpirationMinutes;
    @Value("${security.jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    /**
     * 로그인 요청을 처리하고, 인증된 사용자에 대한 액세스 토큰과 리프레시 토큰을 생성한다.
     *
     * @param loginRequest 사용자 로그인 요청 Request (사용자 로그인 ID 및 비밀번호)
     * @return 로그인 응답 정보 (액세스 토큰 및 리프레시 토큰)
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );

        User user = userUseCase.getUserByUserName(loginRequest.getUserName());

        String accessToken = tokenHandler.generateToken(user.getUserName(), accessExpirationMinutes * 60 * 1000L);
        String refreshToken = tokenHandler.generateToken(user.getUserName(), refreshExpirationDays * 24 * 60 * 60 * 1000L);

        // Refresh Token을 Redis에 저장
        cacheStore.put(
            JWT_REFRESH_TOKEN_PREFIX + user.getUserName(),
            refreshToken,
            Duration.of(refreshExpirationDays, TimeUnit.DAYS.toChronoUnit()));

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    /**
     * 사용자 등록 요청을 처리한다. 사용자가 존재하지 않는 경우에만 새 사용자를 저장한다.
     *
     * @param registerRequest 사용자 등록 요청 Request (사용자 로그인 ID 및 비밀번호)
     */
    @Transactional
    public void register(RegisterRequest registerRequest) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        if(userUseCase.isUserExists(registerRequest.getUserName())) {
            throw new BusinessException(
                BusinessErrorCode.EXIST_USER,
                "userName: " + registerRequest.getUserName());
        }
        userUseCase.saveUser(userDtoMapper.toDomain(registerRequest));
    }

    public ReissueResponse reissue(String refreshToken) {

        String userId = tokenHandler.getIdFromToken(refreshToken);
        String savedRefreshToken = cacheStore.get(JWT_REFRESH_TOKEN_PREFIX + userId, String.class);
        if (ObjectUtils.isEmpty(savedRefreshToken) || !refreshToken.equals(savedRefreshToken)) {
            throw new BusinessException(BusinessErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = tokenHandler.generateToken(userId, accessExpirationMinutes * 60 * 1000L);

        return ReissueResponse.builder()
            .accessToken(newAccessToken)
            .build();
    }

}
