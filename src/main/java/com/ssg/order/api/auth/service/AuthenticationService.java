package com.ssg.order.api.auth.service;

import com.ssg.order.api.user.service.request.LoginRequest;
import com.ssg.order.domain.user.User;
import com.ssg.order.domain.user.usecase.UserUseCase;
import com.ssg.order.infra.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserUseCase userUseCase;
    private final JwtUtil jwtUtil;

    public String authenticate(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
        );

        User user = userUseCase.findUserByUserName(loginRequest.getUserName());
        return jwtUtil.generateToken(user.getUserName());
    }
} 