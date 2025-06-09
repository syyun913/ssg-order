package com.ssg.order.api.user.service.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "access 토큰 재발행 response")
@Builder
@Getter
public class ReissueResponse {
    @Schema(description = "재발행 된 access 토큰")
    private String accessToken;
}
