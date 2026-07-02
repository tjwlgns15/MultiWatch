package com.sjh.multiwatch.infrastructure.websocket;

import com.sjh.multiwatch.domain.alert.Alert;

import java.time.LocalDateTime;

public record AlertBroadcastPayload(
        Long alertRuleId,
        Double triggeredValue,
        LocalDateTime triggeredAt
) {
    public static AlertBroadcastPayload from(Alert alert) {
        return new AlertBroadcastPayload(alert.getAlertRuleId(), alert.getTriggeredValue(), alert.getTriggeredAt());
    }
}