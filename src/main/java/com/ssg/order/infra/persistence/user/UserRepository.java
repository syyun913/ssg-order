package com.ssg.order.infra.persistence.user;

import com.ssg.order.domain.user.User;
import com.ssg.order.domain.user.repository.UserReadRepository;
import com.ssg.order.domain.user.repository.UserWriteRepository;
import com.ssg.order.infra.persistence.user.entity.UserEntity;
import com.ssg.order.infra.persistence.user.mapper.UserPersistenceMapper;
import com.ssg.order.infra.persistence.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UserReadRepository, UserWriteRepository {
    private final UserPersistenceMapper mapper;
    private final UserJpaRepository userJpaRepository;

    @Override
    public User findUserByUserName(String userName) {
        UserEntity userEntity = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + userName));

        // TODO: 공통 예외 적용
        return mapper.toDomain(userEntity);
    }

    @Override
    public boolean isExistUser(String userName) {
        return !ObjectUtils.isEmpty(userJpaRepository.findByUserName(userName));
    }


    @Override
    public void saveUser(User user) {
        userJpaRepository.save(mapper.toEntity(user));
    }
}
