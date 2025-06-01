package com.ssg.order.api.user.service;

import com.ssg.order.api.user.mapper.UserDtoMapper;
import com.ssg.order.api.user.service.request.RegisterRequest;
import com.ssg.order.domain.user.usecase.UserUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSerivce {
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;
    private final UserUseCase userUseCase;

    @Transactional
    public void register(RegisterRequest registerRequest) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // TODO: 공통 예외로 변경
        if(userUseCase.isUserExists(registerRequest.getUserName())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        userUseCase.saveUser(userDtoMapper.registerRequestToDomain(registerRequest));
    }
}
