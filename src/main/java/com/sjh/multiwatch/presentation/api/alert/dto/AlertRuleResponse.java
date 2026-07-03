package com.sjh.multiwatch.presentation.api.alert.dto;

import com.sjh.multiwatch.domain.alert.AlertRule;
import com.sjh.multiwatch.domain.alert.Comparator;

public record AlertRuleResponse(
        Long id,
        Long deviceId,
        Double thresholdValue,
        Comparator comparator,
        boolean enabled
) {
    public static AlertRuleResponse from(AlertRule rule) {
        return new AlertRuleResponse(
                rule.getId(),
                rule.getDeviceId(),
                rule.getThresholdValue(),
                rule.getComparator(),
                rule.isEnabled()
        );
    }
}
