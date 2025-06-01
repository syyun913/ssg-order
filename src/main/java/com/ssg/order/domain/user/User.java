package com.ssg.order.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private final Long id;
    private final String name;
}
