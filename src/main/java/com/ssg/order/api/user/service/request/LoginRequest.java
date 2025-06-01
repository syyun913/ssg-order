package com.ssg.order.api.user.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Schema(description = "로그인 request")
@Getter
public class LoginRequest {
    @Schema(description = "사용자 ID")
    @NotEmpty
    private String userName;

    @Schema(description = "사용자 비밀번호")
    @NotEmpty
    private String password;
}
