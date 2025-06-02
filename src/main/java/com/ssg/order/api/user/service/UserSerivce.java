package com.ssg.order.api.user.service;

import com.ssg.order.api.user.mapper.UserDtoMapper;
import com.ssg.order.api.user.service.request.RegisterRequest;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
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

        if(userUseCase.isUserExists(registerRequest.getUserName())) {
            throw new BusinessException(
                BusinessErrorCode.EXIST_USER,
                "userName: " + registerRequest.getUserName());
        }
        userUseCase.saveUser(userDtoMapper.registerRequestToDomain(registerRequest));
    }
}
