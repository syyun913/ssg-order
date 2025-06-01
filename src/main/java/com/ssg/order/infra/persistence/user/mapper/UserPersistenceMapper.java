package com.ssg.order.infra.persistence.user.mapper;

import com.ssg.order.domain.user.User;
import com.ssg.order.infra.persistence.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {
    public User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return User.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .build();
    }
}
