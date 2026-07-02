package com.sjh.multiwatch.infrastructure.security.filter;

import com.sjh.multiwatch.domain.organization.OrganizationRepository;
import com.sjh.multiwatch.infrastructure.security.DeviceGatewayPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 *  apiKey로 DB 조회 성공 시 검증 완료된 상태로 간주,
 *  이 시점엔 이미 인증된 결과를 SecurityContext에 등록
 */
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private final OrganizationRepository organizationRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null) {
            organizationRepository.findByApiKey(apiKey).ifPresent(org -> {
                DeviceGatewayPrincipal principal = DeviceGatewayPrincipal.from(org);
                var authentication = new PreAuthenticatedAuthenticationToken(principal, null, List.of(new SimpleGrantedAuthority("ROLE_GATEWAY")));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        filterChain.doFilter(request, response);
    }
}
