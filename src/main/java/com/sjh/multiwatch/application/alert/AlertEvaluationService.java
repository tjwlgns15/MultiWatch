package com.sjh.multiwatch.application.alert;

import com.sjh.multiwatch.domain.alert.Alert;
import com.sjh.multiwatch.domain.alert.AlertRepository;
import com.sjh.multiwatch.domain.alert.AlertRule;
import com.sjh.multiwatch.domain.alert.AlertRuleRepository;
import com.sjh.multiwatch.infrastructure.kafka.ReadingMessage;
import com.sjh.multiwatch.infrastructure.websocket.AlertBroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertEvaluationService {

    private final AlertRuleRepository alertRuleRepository;
    private final AlertRepository alertRepository;
    private final AlertBroadcastService alertBroadcastService;


    @Transactional
    public void evaluate(ReadingMessage message) {
        List<AlertRule> rules = alertRuleRepository.findByDeviceIdAndEnabledTrue(message.deviceId());

        for (AlertRule rule : rules) {
            if (rule.isViolatedBy(message.value())) {
                raiseAlert(rule, message);
            }
        }
    }

    private void raiseAlert(AlertRule rule, ReadingMessage message) {
        Alert alert = Alert.raise(rule.getId(), message.organizationId(), message.value(), message.recordedAt());
        alertRepository.save(alert);
        alertBroadcastService.broadcast(alert);
    }
}
