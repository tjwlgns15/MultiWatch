package com.sjh.multiwatch.infrastructure.redis.alert;

import com.sjh.multiwatch.domain.alert.AlertRule;
import com.sjh.multiwatch.domain.alert.Comparator;

/**
 * Redis에 캐싱되는 AlertRule의 읽기 전용 스냅샷.
 * 엔티티를 직접 캐싱하지 않는 이유: JPA 프록시/지연로딩 필드가
 * 역직렬화 시 문제를 일으킬 수 있어 순수 DTO로 분리
 */
public record AlertRuleCacheEntry(
        Long id,
        Long deviceId,
        Double thresholdValue,
        Comparator comparator,
        boolean enabled
) {

    public static AlertRuleCacheEntry from(AlertRule alertRule) {
        return new AlertRuleCacheEntry(
                alertRule.getId(),
                alertRule.getDeviceId(),
                alertRule.getThresholdValue(),
                alertRule.getComparator(),
                alertRule.isEnabled()
        );
    }

    public boolean isViolatedBy(Double value) {
        return enabled && comparator.isViolated(value, thresholdValue);
    }
}