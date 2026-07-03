package com.sjh.multiwatch.presentation.api.organization.dto;

import com.sjh.multiwatch.domain.organization.Organization;

public record SignUpResponse(
        Long organizationId,
        String apiKey
) {
    public static SignUpResponse from(Organization organization) {
        return new SignUpResponse(organization.getId(), organization.getApiKey());
    }
}