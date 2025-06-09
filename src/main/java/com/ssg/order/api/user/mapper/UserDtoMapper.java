package com.ssg.order.api.user.mapper;

import com.ssg.order.api.user.service.request.RegisterRequest;
import com.ssg.order.domain.domain.user.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class UserDtoMapper {
    public User toDomain(RegisterRequest registerRequest) {
        if (ObjectUtils.isEmpty(registerRequest)) {
            return null;
        }
        return User.builder()
                .userName(registerRequest.getUserName())
                .password(registerRequest.getPassword())
                .build();
    }
}
