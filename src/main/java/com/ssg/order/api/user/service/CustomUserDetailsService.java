package com.ssg.order.api.user.service;

import com.ssg.order.api.global.LoginUser;
import com.ssg.order.domain.user.User;
import com.ssg.order.domain.user.usecase.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserUseCase userUseCase;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userUseCase.findUserByUserName(username);

        return LoginUser.builder()
            .id(user.getId())
            .username(user.getUserName())
            .password(user.getPassword())
            .build();
    }
}