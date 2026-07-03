package com.sjh.multiwatch.presentation.api.organization;

import com.sjh.multiwatch.application.organization.OrganizationService;
import com.sjh.multiwatch.domain.organization.Organization;
import com.sjh.multiwatch.presentation.api.organization.dto.SignUpRequest;
import com.sjh.multiwatch.presentation.api.organization.dto.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        Organization organization = organizationService.signUp(request);
        SignUpResponse response = SignUpResponse.from(organization);
        return ResponseEntity.ok(response);
    }
}