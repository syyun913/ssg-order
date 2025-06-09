package com.ssg.order.infrastructure.persistence.user.mapper;

import com.ssg.order.domain.domain.user.User;
import com.ssg.order.infrastructure.persistence.user.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class UserPersistenceMapper {
    public User toDomain(UserEntity userEntity) {
        if (ObjectUtils.isEmpty(userEntity)) {
            return null;
        }
        return User.builder()
                .id(userEntity.getId())
                .userName(userEntity.getUserName())
                .password(userEntity.getPassword())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (ObjectUtils.isEmpty(user)) {
            return null;
        }
        return UserEntity.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .build();
    }
}
