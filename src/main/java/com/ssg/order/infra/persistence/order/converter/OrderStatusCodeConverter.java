package com.ssg.order.infra.persistence.order.converter;

import com.ssg.order.domain.order.enumtype.OrderStatusCode;
import jakarta.persistence.AttributeConverter;

public class OrderStatusCodeConverter implements AttributeConverter<OrderStatusCode, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatusCode attribute) {
        return attribute.getValue();
    }

    @Override
    public OrderStatusCode convertToEntityAttribute(String dbData) {
        return OrderStatusCode.ofValue(dbData);
    }
}
