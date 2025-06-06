package com.ssg.order.domain.domain.user.repository;

import com.ssg.order.domain.domain.user.User;

public interface UserReadRepository {
    User getUserByUserName(String userName);

    boolean isExistUser(String userName);

}
