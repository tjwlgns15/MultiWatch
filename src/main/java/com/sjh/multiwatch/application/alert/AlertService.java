package com.sjh.multiwatch.application.alert;

import com.sjh.multiwatch.domain.alert.Alert;
import com.sjh.multiwatch.domain.alert.AlertRepository;
import com.sjh.multiwatch.infrastructure.exception.CustomException;
import com.sjh.multiwatch.infrastructure.security.aop.TenantScoped;
import com.sjh.multiwatch.presentation.alert.dto.AlertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjh.multiwatch.infrastructure.exception.ErrorCode.ALERT_NOT_FOUND;

@Service
@TenantScoped
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertRepository;


    public List<Alert> getUnacknowledgedAlerts() {
        return alertRepository.findByAcknowledgedFalseOrderByTriggeredAtDesc();
    }

    @Transactional
    public void acknowledge(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new CustomException(ALERT_NOT_FOUND));
        alert.acknowledge();
    }
}