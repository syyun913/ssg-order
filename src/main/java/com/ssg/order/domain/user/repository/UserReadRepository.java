package com.ssg.order.domain.user.repository;

import com.ssg.order.domain.user.User;

public interface UserReadRepository {
    User findUserByUserName(String userName);

    boolean isExistUser(String userName);

}
