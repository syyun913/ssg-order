package com.ssg.order.api.user.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원가입 request")
@Getter
public class RegisterRequest {
    @Schema(description = "사용자 ID")
    @NotEmpty
    private String userName;

    @Schema(description = "사용자 비밀번호")
    @NotEmpty
    @Setter
    private String password;
}
