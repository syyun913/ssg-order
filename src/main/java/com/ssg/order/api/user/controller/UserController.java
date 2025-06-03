package com.ssg.order.api.user.controller;

import com.ssg.order.api.user.controller.response.LoginResponse;
import com.ssg.order.api.global.common.response.CommonResponse;
import com.ssg.order.api.user.service.UserSerivce;
import com.ssg.order.api.user.service.request.LoginRequest;
import com.ssg.order.api.user.service.request.RegisterRequest;
import com.ssg.order.api.user.service.request.ReissueRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 서비스", description = "사용자 관련 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserSerivce userSerivce;

    @Operation(summary = "사용자 로그인")
    @PostMapping("/login")
    public ResponseEntity<CommonResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse loginResponse = userSerivce.login(request);

        return ResponseEntity.ok(CommonResponse.of("로그인에 성공하였습니다.", loginResponse));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<CommonResponse> register(@RequestBody @Valid RegisterRequest request) {
        userSerivce.register(request);
        return ResponseEntity.ok(CommonResponse.of("회원가입에 성공하였습니다.", null));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // 로그아웃 로직은 LogoutService에서 처리
        return ResponseEntity.ok(CommonResponse.of("로그아웃에 성공하였습니다.", null));
    }

    @Operation(summary = "access 토큰 재발행")
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse> reissue(@RequestBody @Valid ReissueRequest request) {
        return ResponseEntity.ok(CommonResponse.of("토큰이 성공적으로 발행되었습니다.", userSerivce.reissue(request.getRefreshToken())));
    }
}
