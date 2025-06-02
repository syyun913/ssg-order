package com.ssg.order.domain.common.annotation.exception;

import com.ssg.order.domain.common.annotation.exception.code.InternalErrorCode;
import java.util.List;
import lombok.Getter;

@Getter
public class InternalServerException extends RuntimeException {

    private InternalErrorCode errorCode;
    private String message;
    private String hint;

    public InternalServerException(InternalErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public InternalServerException(InternalErrorCode errorCode, String hint) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
        this.hint = hint;
    }

    public InternalServerException(
        InternalErrorCode errorCode,
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
