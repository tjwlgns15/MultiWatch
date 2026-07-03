package com.sjh.multiwatch.presentation.api.alert.dto;

import com.sjh.multiwatch.domain.alert.Comparator;

public record RegisterAlertRuleRequest (
        Long deviceId,
        Double thresholdValue,
        Comparator comparator
){}
