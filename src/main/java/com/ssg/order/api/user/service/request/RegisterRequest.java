package com.ssg.order.api.user.service.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원가입 request")
@Getter
public class RegisterRequest {
    @Schema(description = "사용자 ID")
    @NotEmpty
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,20}$", 
            message = "아이디는 영문, 숫자 조합 4~20자로 입력해주세요.")
    private String userName;

    @Schema(description = "사용자 비밀번호")
    @NotEmpty
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$", 
            message = "비밀번호는 영문, 숫자, 특수문자 조합 8~20자로 입력해주세요.")
    @Setter
    private String password;
}
