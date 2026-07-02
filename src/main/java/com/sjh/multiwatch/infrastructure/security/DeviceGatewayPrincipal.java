package com.sjh.multiwatch.infrastructure.security;

import com.sjh.multiwatch.domain.organization.Organization;
import lombok.Getter;

/**
 * 신호 송/수신용 게이트웨이 인증 객체
 */
@Getter
public class DeviceGatewayPrincipal {


    private final Long organizationId;
    private final String apiKey;

    private DeviceGatewayPrincipal(Long organizationId, String apiKey) {
        this.organizationId = organizationId;
        this.apiKey = apiKey;
    }

    public static DeviceGatewayPrincipal from(Organization organization) {
        return new DeviceGatewayPrincipal(organization.getId(), organization.getApiKey());
    }
}
