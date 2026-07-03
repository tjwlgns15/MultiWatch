package com.sjh.multiwatch.presentation.organization.api;

import com.sjh.multiwatch.application.organization.OrganizationService;
import com.sjh.multiwatch.presentation.organization.dto.SignUpRequest;
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
    public ResponseEntity<Long> signUp(@RequestBody SignUpRequest request) {
        Long organizationId = organizationService.signUp(request);
        return ResponseEntity.ok(organizationId);
    }
}