package com.ssg.order.domain.domain.user.repository;

import com.ssg.order.domain.domain.user.User;

public interface UserWriteRepository {
    void saveUser(User user);
}
