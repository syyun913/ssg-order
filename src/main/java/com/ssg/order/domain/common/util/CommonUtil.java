package com.ssg.order.domain.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommonUtil {
    public static <T, R> List<R> extractIds(
        List<T> list,
        Function<T, R> mapper
    ) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream()
                   .map(mapper)
                   .collect(Collectors.toList());
    }

    public static <T, K> Map<K, T> convertToMap(
        List<T> domain,
        Function<T, K> mapper
    ) {
        if (domain == null) {
            return Collections.emptyMap();
        }
        return domain.stream()
                     .collect(Collectors.toMap(
                         mapper,
                         entity -> entity
                     ));
    }

    public static <T, K, V> Map<K, V> convertKeyAndValueToMap(
        List<T> domain,
        Function<T, K> keyMapper,
        Function<T, V> valueMapper
    ) {
        if (domain == null) {
            return Collections.emptyMap();
        }
        return domain.stream()
                     .collect(Collectors.toMap(
                         keyMapper,
                         valueMapper
                     ));
    }
}
