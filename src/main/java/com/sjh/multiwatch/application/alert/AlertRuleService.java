package com.sjh.multiwatch.application.alert;

import com.sjh.multiwatch.domain.alert.AlertRule;
import com.sjh.multiwatch.domain.alert.AlertRuleRepository;
import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.infrastructure.exception.CustomException;
import com.sjh.multiwatch.infrastructure.exception.ErrorCode;
import com.sjh.multiwatch.infrastructure.redis.alert.AlertRuleCacheRepository;
import com.sjh.multiwatch.infrastructure.security.aop.TenantScoped;
import com.sjh.multiwatch.presentation.api.alert.dto.RegisterAlertRuleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@TenantScoped
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;
    private final DeviceRepository deviceRepository;
    private final AlertRuleCacheRepository alertRuleCacheRepository;

    @Transactional
    public Long createRule(RegisterAlertRuleRequest request, Long organizationId) {
        Device device = deviceRepository.findByIdAndOrganizationId(request.deviceId(), organizationId)
                .orElseThrow(() -> new CustomException(ErrorCode.DEVICE_NOT_FOUND));

        AlertRule alertRule = AlertRule.create(device.getId(), request.thresholdValue(), request.comparator());
        Long ruleId = alertRuleRepository.save(alertRule).getId();

        alertRuleCacheRepository.evict(device.getId());

        return ruleId;
    }

    public List<AlertRule> getRulesByDevice(Long deviceId, Long organizationId) {
        deviceRepository.findByIdAndOrganizationId(deviceId, organizationId)
                .orElseThrow(() -> new CustomException(ErrorCode.DEVICE_NOT_FOUND));

        return alertRuleRepository.findByDeviceId(deviceId);
    }

    @Transactional
    public void disableRule(Long ruleId, Long organizationId) {
        AlertRule alertRule = alertRuleRepository.findByIdAndOrganizationId(ruleId, organizationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALERT_RULE_NOT_FOUND));

        alertRule.disable();
        alertRuleCacheRepository.evict(alertRule.getDeviceId());
    }
}