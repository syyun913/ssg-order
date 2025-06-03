package com.ssg.order.domain.common.annotation.exception;

import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import java.util.List;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private BusinessErrorCode errorCode;
    private String message;
    private String hint;

    public BusinessException(BusinessErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public BusinessException(
        BusinessErrorCode errorCode,
        String hint
    ) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.hint = hint;
    }

    public BusinessException(
        BusinessErrorCode errorCode,
        List params
    ) {
        this.errorCode = errorCode;
        this.message = replaceMessage(
            errorCode.getMessage(),
            params
        );
    }

    private String replaceMessage(
        String message,
        List params
    ) {
        for (Object param : params) {
            message = message.replaceFirst(
                "\\{param}",
                param.toString()
            );
        }

        return message;
    }
}
