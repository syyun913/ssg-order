package com.ssg.order.api.global.core.exception;

import com.ssg.order.api.global.common.response.ExceptionResponse;
import com.ssg.order.domain.common.annotation.exception.InternalServerException;
import com.ssg.order.domain.common.annotation.exception.code.InternalErrorCode;
import com.ssg.order.domain.common.annotation.exception.BusinessException;
import com.ssg.order.domain.common.annotation.exception.code.BusinessErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<?> handleNoResourceFoundException(
        final NoResourceFoundException exception
    ) {
        return ExceptionResponse.server(
            HttpStatus.NOT_FOUND,
            exception.getMessage()
        );
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ExceptionResponse> handleBindException(BindException exception) {
        String message = getMessageFromBindingResult(exception.getBindingResult());

        return ExceptionResponse.server(
            HttpStatus.BAD_REQUEST,
            message
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException exception
    ) {
        String message = getMessageFromBindingResult(exception.getBindingResult());

        return ExceptionResponse.server(
            HttpStatus.BAD_REQUEST,
            message
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException exception
    ) {
        String message = String.format(
            "%s(은)는 필수입니다.",
            exception.getParameterName()
        );

        return ExceptionResponse.server(
            HttpStatus.BAD_REQUEST,
            message
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ExceptionResponse> handlerMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException exception
    ) {
        return ExceptionResponse.server(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException exception
    ) {
        return ExceptionResponse.server(
            HttpStatus.METHOD_NOT_ALLOWED,
            exception.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        return ExceptionResponse.server(
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception.getMessage()
        );
    }

    /**
     * 비즈니스 로직 에러
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
        BusinessErrorCode errorCode = e.getErrorCode();

        return ExceptionResponse.toResponseEntity(
            errorCode.getHttpStatus(),
            errorCode.name(),
            e.getMessage(),
            e.getHint()
        );
    }

    /**
     * 사용자 정의 예외 처리
     */
    @ExceptionHandler(InternalServerException.class)
    protected ResponseEntity<ExceptionResponse> handleInternalServerException(
        InternalServerException e
    ) {
        InternalErrorCode errorCode = e.getErrorCode();

        return ExceptionResponse.toResponseEntity(
            errorCode.getHttpStatus(),
            errorCode.name(),
            e.getMessage(),
            e.getHint()
        );
    }

    protected String getMessageFromBindingResult(BindingResult bindingResult) {
        FieldError error = bindingResult.getFieldErrors().get(0);

        StringBuilder builder = new StringBuilder();
        builder.append(error.getField());
        builder.append("(은)는 ");
        builder.append(error.getDefaultMessage());
        builder.append(". 입력된 값: [");
        builder.append(error.getRejectedValue());
        builder.append("]");

        return builder.toString();
    }
}
