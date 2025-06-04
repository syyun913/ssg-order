package com.ssg.order.api.user.mapper;

import com.ssg.order.api.user.service.request.RegisterRequest;
import com.ssg.order.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public User toDomain(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            return null;
        }
        return User.builder()
                .userName(registerRequest.getUserName())
                .password(registerRequest.getPassword())
                .build();
    }
}
