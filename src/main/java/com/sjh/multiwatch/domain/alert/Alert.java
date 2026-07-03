package com.sjh.multiwatch.domain.alert;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@NoArgsConstructor
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "orgId", type = Long.class))
@Filter(name = "tenantFilter", condition = "organization_id = :orgId")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_rule_id", nullable = false)
    private Long alertRuleId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Double triggeredValue;

    @Column(nullable = false)
    private LocalDateTime triggeredAt;

    @Column(nullable = false)
    private boolean acknowledged;

    private Alert(Long alertRuleId, Long organizationId, Double triggeredValue, LocalDateTime triggeredAt) {
        this.alertRuleId = alertRuleId;
        this.organizationId = organizationId;
        this.triggeredValue = triggeredValue;
        this.triggeredAt = triggeredAt;
        this.acknowledged = false;
    }

    public static Alert raise(Long alertRuleId, Long organizationId, Double triggeredValue, LocalDateTime triggeredAt) {
        return new Alert(alertRuleId, organizationId, triggeredValue, triggeredAt);
    }

    public void acknowledge() {
        this.acknowledged = true;
    }
}
