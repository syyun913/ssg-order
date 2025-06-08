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

    /**
     * 사용자 정보를 조회합니다.
     * @param userName 사용자 로그인 ID
     * @return 사용자 정보
     */
    @Override
    public User getUserByUserName(String userName) {
        return userReadRepository.getUserByUserName(userName);
    }

    /**
     * 사용자 정보를 저장합니다.
     * @param user 사용자 정보
     */
    @Override
    public void saveUser(User user) {
        userWriteRepository.saveUser(user);
    }

    /**
     * 사용자 존재 여부를 확인합니다.
     * @param userName 사용자 로그인 ID
     * @return 사용자 존재 여부
     */
    @Override
    public boolean isUserExists(String userName) {
        return userReadRepository.isExistUser(userName);
    }
}
