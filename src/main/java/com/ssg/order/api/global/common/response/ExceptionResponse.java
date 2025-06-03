package com.ssg.order.api.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Schema(description = "예외 response")
@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class ExceptionResponse {

    @Schema(description = "상태 코드", example = "400")
    @JsonProperty("response_status_code")
    private final int status;

    @Schema(description = "예외 코드", example = "INTERNAL_400_3")
    private final String code;

    @Schema(description = "예외 메시지", example = "잘못된 요청입니다.")
    private final String message;

    @Schema(description = "예외 힌트", example = "unsupported error")
    private final String hint;

    public static ResponseEntity<ExceptionResponse> toResponseEntity(
        HttpStatus status,
        String message
    ) {
        return ResponseEntity.status(status)
            .body(ExceptionResponse.builder()
                .status(status.value())
                .message(message)
                .build());
    }

    public static ResponseEntity<ExceptionResponse> toResponseEntity(
        HttpStatus status,
        String code,
        String message,
        String hint
    ) {
        return ResponseEntity.status(status)
            .body(ExceptionResponse.builder()
                .status(status.value())
                .code(code)
                .message(message)
                .hint(hint)
                .build());
    }

    public static ResponseEntity<ExceptionResponse> server(
        HttpStatus status,
        String message
    ) {
        return ResponseEntity.status(status)
            .body(ExceptionResponse.builder()
                .status(status.value())
                .message(message)
                .build());
    }
}
