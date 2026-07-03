package com.sjh.multiwatch.presentation.api.alert;

import com.sjh.multiwatch.application.alert.AlertRuleService;
import com.sjh.multiwatch.infrastructure.security.MemberPrincipal;
import com.sjh.multiwatch.presentation.api.alert.dto.AlertRuleResponse;
import com.sjh.multiwatch.presentation.api.alert.dto.RegisterAlertRuleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert-rules")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    @PostMapping
    public ResponseEntity<Long> register(
            @RequestBody RegisterAlertRuleRequest request,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long ruleId = alertRuleService.createRule(request, principal.getOrganizationId());
        return ResponseEntity.ok(ruleId);
    }

    @GetMapping
    public ResponseEntity<List<AlertRuleResponse>> getByDevice(
            @RequestParam Long deviceId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        List<AlertRuleResponse> responses = alertRuleService.getRulesByDevice(deviceId, principal.getOrganizationId()).stream()
                .map(AlertRuleResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> disable(
            @PathVariable Long ruleId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        alertRuleService.disableRule(ruleId, principal.getOrganizationId());
        return ResponseEntity.ok().build();
    }
}