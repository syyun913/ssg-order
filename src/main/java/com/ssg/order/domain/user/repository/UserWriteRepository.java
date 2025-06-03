package com.ssg.order.domain.user.repository;

import com.ssg.order.domain.user.User;

public interface UserWriteRepository {
    void saveUser(User user);
}
