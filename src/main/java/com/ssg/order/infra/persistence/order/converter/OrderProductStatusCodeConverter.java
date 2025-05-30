package com.ssg.order.infra.persistence.order.converter;

import com.ssg.order.domain.order.enumtype.OrderProductStatusCode;
import jakarta.persistence.AttributeConverter;

public class OrderProductStatusCodeConverter implements AttributeConverter<OrderProductStatusCode, String> {
    @Override
    public String convertToDatabaseColumn(OrderProductStatusCode attribute) {
        return attribute.getValue();
    }

    @Override
    public OrderProductStatusCode convertToEntityAttribute(String dbData) {
        return OrderProductStatusCode.ofValue(dbData);
    }
}
