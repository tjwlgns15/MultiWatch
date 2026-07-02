package com.sjh.multiwatch.domain.alert;

import com.sjh.multiwatch.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alert_rules")
@Getter
@NoArgsConstructor
public class AlertRule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(nullable = false)
    private Double thresholdValue;

    @Enumerated(EnumType.STRING)
    private Comparator comparator;

    @Column(nullable = false)
    private boolean enabled;

    private AlertRule(Long deviceId, Double thresholdValue, Comparator comparator) {
        this.deviceId = deviceId;
        this.thresholdValue = thresholdValue;
        this.comparator = comparator;
        this.enabled = true;
    }

    public static AlertRule create(Long deviceId, Double thresholdValue, Comparator comparator) {
        return new AlertRule(deviceId, thresholdValue, comparator);
    }

    public boolean isViolatedBy(Double value) {
        return enabled && comparator.isViolated(value, thresholdValue);
    }

    public void disable() {
        this.enabled = false;
    }
}
