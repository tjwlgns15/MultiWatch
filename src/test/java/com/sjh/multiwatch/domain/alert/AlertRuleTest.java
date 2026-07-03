package com.sjh.multiwatch.domain.alert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlertRuleTest {

    @Test
    @DisplayName("GT 비교에서 값이 임계치보다 크면 위반으로 판단한다")
    void isViolatedBy_greaterThan() {
        AlertRule rule = AlertRule.create(1L, 30.0, Comparator.GT);

        assertThat(rule.isViolatedBy(35.0)).isTrue();
        assertThat(rule.isViolatedBy(25.0)).isFalse();
        assertThat(rule.isViolatedBy(30.0)).isFalse();
    }

    @Test
    @DisplayName("LT 비교에서 값이 임계치보다 작으면 위반으로 판단한다")
    void isViolatedBy_lessThan() {
        AlertRule rule = AlertRule.create(1L, 10.0, Comparator.LT);

        assertThat(rule.isViolatedBy(5.0)).isTrue();
        assertThat(rule.isViolatedBy(15.0)).isFalse();
    }

    @Test
    @DisplayName("비활성화된 규칙은 값과 무관하게 위반으로 판단하지 않는다")
    void isViolatedBy_disabledRule() {
        AlertRule rule = AlertRule.create(1L, 30.0, Comparator.GT);
        rule.disable();

        assertThat(rule.isViolatedBy(999.0)).isFalse();
    }

    @Test
    @DisplayName("생성 직후 규칙은 활성화 상태다")
    void create_enabledByDefault() {
        AlertRule rule = AlertRule.create(1L, 30.0, Comparator.GT);

        assertThat(rule.isEnabled()).isTrue();
    }
}