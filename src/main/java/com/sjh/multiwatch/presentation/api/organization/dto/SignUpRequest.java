package com.sjh.multiwatch.presentation.api.organization.dto;

public record SignUpRequest(
        String organizationName,
        String adminEmail,
        String adminPassword
) {
}
