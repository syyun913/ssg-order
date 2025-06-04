package com.ssg.order.api.user.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "로그인 response")
@Builder
@Getter
public class LoginResponse {
    @Schema(description = "API 요청 시 사용되는 토큰")
    private String accessToken;

    @Schema(description = "기존 토큰 만료 시 재발급 위한 토큰")
    private String refreshToken;
}
