package com.ssg.order.api.global.core.config;

import static com.ssg.order.domain.common.annotation.constants.CommonConstants.UNAUTHORIZED_MESSAGE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.order.api.user.service.LogoutService;
import com.ssg.order.api.global.common.response.ExceptionResponse;
import com.ssg.order.api.global.core.filter.JwtAuthenticationFilter;
import java.io.PrintWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private LogoutService logoutService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(f -> f.disable()))
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/**", "/view/**", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs", "/h2-console/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor((request, response, authException) -> {
                    if (request.getRequestURI().equals("/")) {
                        response.sendRedirect("/view/user/login-page");
                    } else {
                        authenticationEntryPoint().commence(request, response, authException);
                    }
                }, request -> true)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout.logoutUrl("/user/logout")
                .addLogoutHandler(logoutService)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            PrintWriter out = response.getWriter();

            out.write(objectMapper.writeValueAsString(
                ExceptionResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message(UNAUTHORIZED_MESSAGE)
                    .build()));

            out.flush();
            out.close();
        };
    }
}