package com.sjh.multiwatch.application.alert;

import com.sjh.multiwatch.domain.alert.Alert;
import com.sjh.multiwatch.domain.alert.AlertRepository;
import com.sjh.multiwatch.infrastructure.kafka.ReadingMessage;
import com.sjh.multiwatch.infrastructure.redis.alert.AlertRuleCacheEntry;
import com.sjh.multiwatch.infrastructure.redis.alert.AlertRuleCacheRepository;
import com.sjh.multiwatch.infrastructure.websocket.AlertBroadcastService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertEvaluationService {

    private final AlertRuleCacheRepository alertRuleCacheRepository;
    private final AlertRepository alertRepository;
    private final AlertBroadcastService alertBroadcastService;

    @Transactional
    public void evaluate(ReadingMessage message) {
        List<AlertRuleCacheEntry> rules = alertRuleCacheRepository.findByDeviceId(message.deviceId());

        for (AlertRuleCacheEntry rule : rules) {
            if (rule.isViolatedBy(message.value())) {
                raiseAlert(rule, message);
            }
        }
    }

    private void raiseAlert(AlertRuleCacheEntry rule, ReadingMessage message) {
        Alert alert = Alert.raise(rule.id(), message.organizationId(), message.value(), message.recordedAt());
        alertRepository.save(alert);
        alertBroadcastService.broadcast(alert, message.deviceId());
    }
}