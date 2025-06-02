package com.ssg.order.api.auth.service;

import static com.ssg.order.domain.common.annotation.constants.CommonConstants.JWT_REFRESH_TOKEN_PREFIX;

import com.ssg.order.api.auth.service.response.LoginResponse;
import com.ssg.order.api.user.service.request.LoginRequest;
import com.ssg.order.domain.cache.CacheStore;
import com.ssg.order.domain.user.User;
import com.ssg.order.domain.user.usecase.UserUseCase;
import com.ssg.order.infra.jwt.util.JwtUtil;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserUseCase userUseCase;
    private final CacheStore cacheStore;
    private final JwtUtil jwtUtil;

    @Value("${security.jwt.access-expiration-minutes}")
    private long accessExpirationMinutes;
    @Value("${security.jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    public LoginResponse authenticate(LoginRequest loginRequest) {
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
} 