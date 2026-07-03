package com.sjh.multiwatch.infrastructure.websocket;

import com.sjh.multiwatch.domain.alert.Alert;
import java.time.LocalDateTime;

public record AlertBroadcastPayload(
        Long alertRuleId,
        Long deviceId,
        Double triggeredValue,
        LocalDateTime triggeredAt
) {
    public static AlertBroadcastPayload from(Alert alert, Long deviceId) {
        return new AlertBroadcastPayload(alert.getAlertRuleId(), deviceId, alert.getTriggeredValue(), alert.getTriggeredAt());
    }
}