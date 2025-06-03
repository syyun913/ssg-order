package com.ssg.order.api.user.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Schema(description = "access 토큰 재발행 request")
@Getter
public class ReissueRequest {
    @Schema(description = "refresh 토큰")
    @NotEmpty
    private String refreshToken;
}
