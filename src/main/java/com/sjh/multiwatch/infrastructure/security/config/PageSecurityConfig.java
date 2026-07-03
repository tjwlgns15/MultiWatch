package com.sjh.multiwatch.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * 브라우저가 직접 요청하는 HTML 페이지 라우트(로그인/대시보드)의 접근 게이트.
 * API 체인(@Order(1))은 미인증 시 401 JSON을, Gateway 체인(@Order(2))은 API Key 인증을 다루고,
 * 이 체인은 페이지 단위 인증 여부에 따른 리다이렉트를 담당한다.
 */
@Configuration
public class PageSecurityConfig {

    @Bean
    @Order(3)
    public SecurityFilterChain pageSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/dashboard", "/dashboard.html", "/login", "/login.html")

                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/login.html").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
}