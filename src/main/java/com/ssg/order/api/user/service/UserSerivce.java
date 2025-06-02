package com.ssg.order.api.user.service;

import static com.ssg.order.domain.common.annotation.constants.CommonConstants.JWT_REFRESH_TOKEN_PREFIX;

import com.ssg.order.api.user.controller.response.LoginResponse;
import com.ssg.order.api.user.mapper.UserDtoMapper;
import com.ssg.order.api.user.service.request.LoginRequest;
import com.ssg.order.api.user.service.request.RegisterRequest;
import com.ssg.order.api.user.service.response.ReissueResponse;
import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import com.ssg.order.domain.user.User;
import com.ssg.order.domain.user.usecase.UserUseCase;
import com.ssg.order.infra.jwt.util.JwtUtil;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSerivce {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;
    private final UserUseCase userUseCase;
    private final CacheStore cacheStore;
    private final JwtUtil jwtUtil;

    @Value("${security.jwt.access-expiration-minutes}")
    private long accessExpirationMinutes;
    @Value("${security.jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );

        User user = userUseCase.findUserByUserName(loginRequest.getUserName());

        String accessToken = jwtUtil.generateToken(user.getUserName(), accessExpirationMinutes * 60 * 1000L);
        String refreshToken = jwtUtil.generateToken(user.getUserName(), refreshExpirationDays * 24 * 60 * 60 * 1000L);

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

    @Transactional
    public void register(RegisterRequest registerRequest) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        if(userUseCase.isUserExists(registerRequest.getUserName())) {
            throw new BusinessException(
                BusinessErrorCode.EXIST_USER,
                "userName: " + registerRequest.getUserName());
        }
        userUseCase.saveUser(userDtoMapper.registerRequestToDomain(registerRequest));
    }

    public ReissueResponse reissue(String refreshToken) {

        String userId = jwtUtil.getIdFromToken(refreshToken);
        String savedRefreshToken = cacheStore.get(JWT_REFRESH_TOKEN_PREFIX + userId, String.class);
        if (ObjectUtils.isEmpty(savedRefreshToken) || !refreshToken.equals(savedRefreshToken)) {
            throw new BusinessException(BusinessErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtUtil.generateToken(userId, accessExpirationMinutes * 60 * 1000L);

        return ReissueResponse.builder()
            .accessToken(newAccessToken)
            .build();
    }

}
