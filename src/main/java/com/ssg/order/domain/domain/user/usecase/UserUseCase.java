package com.ssg.order.domain.domain.user.usecase;

import com.ssg.order.domain.domain.user.User;

public interface UserUseCase {
    User findUserByUserName(String userName);
    void saveUser(User user);
    boolean isUserExists(String userName);
}
