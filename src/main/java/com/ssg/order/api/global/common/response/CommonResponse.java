package com.ssg.order.api.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "공통 response")
@Getter
@NoArgsConstructor
public class CommonResponse<T> {
    @Schema(description = "상태", example = "ok")
    private String status;

    @Schema(description = "메시지", example = "응답 메시지")
    private String message;

    @Schema(description = "response data")
    private T data;

    @Builder
    private CommonResponse(
            String status,
            String message,
            T data
    ) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> of(
            String message,
            T data
    ) {
        return new CommonResponse<>(
                "ok",
                message,
                data
        );
    }

    public static <T> CommonResponse<T> of(T data) {
        return of(
                null,
                data
        );
    }
}
