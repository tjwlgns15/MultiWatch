package com.sjh.multiwatch.presentation.api.alert;

import com.sjh.multiwatch.application.alert.AlertService;
import com.sjh.multiwatch.presentation.api.alert.dto.AlertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<AlertResponse>> list() {
        List<AlertResponse> responses = alertService.getUnacknowledgedAlerts().stream()
                .map(AlertResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{alertId}/ack")
    public ResponseEntity<Void> acknowledge(@PathVariable Long alertId) {
        alertService.acknowledge(alertId);
        return ResponseEntity.ok().build();
    }
}