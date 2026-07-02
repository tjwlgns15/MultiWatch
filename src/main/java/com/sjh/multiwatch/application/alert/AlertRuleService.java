package com.sjh.multiwatch.application.alert;

import com.sjh.multiwatch.domain.alert.AlertRule;
import com.sjh.multiwatch.domain.alert.AlertRuleRepository;
import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.infrastructure.exception.CustomException;
import com.sjh.multiwatch.infrastructure.exception.ErrorCode;
import com.sjh.multiwatch.infrastructure.security.aop.TenantScoped;
import com.sjh.multiwatch.presentation.alert.dto.RegisterAlertRuleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@TenantScoped
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;
    private final DeviceRepository deviceRepository;


    @Transactional
    public Long createRule(RegisterAlertRuleRequest request) {
        Device device = deviceRepository.findById(request.deviceId())
                .orElseThrow(() -> new CustomException(ErrorCode.DEVICE_NOT_FOUND));

        AlertRule alertRule = AlertRule.create(device.getId(), request.thresholdValue(), request.comparator());
        return alertRuleRepository.save(alertRule).getId();
    }

    @Transactional
    public void disableRule(Long ruleId) {
        AlertRule alertRule = alertRuleRepository.findById(ruleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALERT_RULE_NOT_FOUND));

        alertRule.disable();
    }
}
