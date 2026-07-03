package com.sjh.multiwatch.presentation.organization.dto;

public record SignUpRequest(
        String organizationName,
        String adminEmail,
        String adminPassword
) {
}
