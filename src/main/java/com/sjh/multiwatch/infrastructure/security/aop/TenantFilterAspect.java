package com.sjh.multiwatch.infrastructure.security.aop;

import com.sjh.multiwatch.infrastructure.security.DeviceGatewayPrincipal;
import com.sjh.multiwatch.infrastructure.security.MemberPrincipal;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 테넌트 격리 검증 필터
 * findAll 같은 일반 조회는 동작하지만, findById 같은 직접 조회에는 적용되지 않는다
 */
@Aspect
@Component
@RequiredArgsConstructor
public class TenantFilterAspect {

    private final EntityManager entityManager;

    @Before("@within(com.sjh.multiwatch.infrastructure.security.aop.TenantScoped)")
    public void enableTenantFilter() {
        Long organizationId = currentOrganizationId();

        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter")
                .setParameter("orgId", organizationId);
    }

    private Long currentOrganizationId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("인증되지 않은 요청에서 테넌트 필터를 활성화할 수 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof MemberPrincipal member) {
            return member.getOrganizationId();
        }
        if (principal instanceof DeviceGatewayPrincipal gateway) {
            return gateway.getOrganizationId();
        }

        throw new IllegalStateException("인증되지 않은 요청에서 테넌트 필터를 활성화할 수 없습니다.");
    }
}
