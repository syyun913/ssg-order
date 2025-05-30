package com.ssg.order.domain.common.enumtype;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;

public interface EnumType {
    String getValue();

    static <E extends Enum<E> & EnumType> E ofValue(Class<E> enumClass, String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(v -> v.getValue().equals(value))
                .findAny()
                .orElseThrow(() -> new RuntimeException(
                        enumClass.getSimpleName() + " 존재하지 않는 코드 입니다. code :: " + value));
    }
}
