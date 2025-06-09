package com.ssg.order.api.global;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Schema(description = "Principal 객체")
@Builder
@Getter
public class LoginUser implements UserDetails {
    @Schema(description = "사용자 ID")
    private final Long id;

    @Schema(description = "사용자 로그인 ID")
    private final String username;

    @Schema(description = "사용자 비밀번호")
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
