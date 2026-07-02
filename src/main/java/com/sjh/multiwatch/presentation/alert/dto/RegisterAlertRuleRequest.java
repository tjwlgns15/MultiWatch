package com.sjh.multiwatch.presentation.alert.dto;

import com.sjh.multiwatch.domain.alert.Comparator;

public record RegisterAlertRuleRequest (
        Long deviceId,
        Double thresholdValue,
        Comparator comparator
){}
