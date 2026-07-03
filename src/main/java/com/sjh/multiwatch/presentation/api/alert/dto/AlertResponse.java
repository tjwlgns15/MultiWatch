package com.sjh.multiwatch.presentation.api.alert.dto;

import com.sjh.multiwatch.domain.alert.Alert;

import java.time.LocalDateTime;

public record AlertResponse(
        Long id,
        Long alertRuleId,
        Double triggeredValue,
        LocalDateTime triggeredAt,
        boolean acknowledged
) {
    public static AlertResponse from(Alert alert) {
        return new AlertResponse(
                alert.getId(), alert.getAlertRuleId(), alert.getTriggeredValue(),
                alert.getTriggeredAt(), alert.isAcknowledged()
        );
    }
}