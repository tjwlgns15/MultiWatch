package com.sjh.multiwatch.presentation.alert.api;

import com.sjh.multiwatch.application.alert.AlertRuleService;
import com.sjh.multiwatch.presentation.alert.dto.RegisterAlertRuleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alert-rules")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertRuleService alertRuleService;


    @PostMapping
    public ResponseEntity<Long> register(@RequestBody RegisterAlertRuleRequest request) {
        Long ruleId = alertRuleService.createRule(request);
        return ResponseEntity.ok(ruleId);
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> disable(@PathVariable Long ruleId) {
        alertRuleService.disableRule(ruleId);
        return ResponseEntity.ok().build();
    }
}
