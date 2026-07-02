package com.sjh.multiwatch.infrastructure.security.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 테넌트(조직) 격리가 필요한 애플리케이션 서비스에 부착.
 * TenantFilterAspect가 이 애노테이션이 붙은 클래스의 메서드 실행 전
 * Hibernate Filter(tenantFilter)를 활성화한다.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantScoped {
}