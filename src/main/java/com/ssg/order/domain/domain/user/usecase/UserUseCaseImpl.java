package com.ssg.order.domain.domain.user.usecase;

import com.ssg.order.domain.common.annotation.UseCase;
import com.ssg.order.domain.domain.user.User;
import com.ssg.order.domain.domain.user.repository.UserReadRepository;
import com.ssg.order.domain.domain.user.repository.UserWriteRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
class UserUseCaseImpl implements UserUseCase {
    private final UserReadRepository userReadRepository;
    private final UserWriteRepository userWriteRepository;

    @Override
    public User getUserByUserName(String userName) {
        return userReadRepository.getUserByUserName(userName);
    }

    @Override
    public void saveUser(User user) {
        userWriteRepository.saveUser(user);
    }

    @Override
    public boolean isUserExists(String userName) {
        return userReadRepository.isExistUser(userName);
    }
}
