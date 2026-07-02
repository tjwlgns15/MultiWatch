package com.sjh.multiwatch.infrastructure.kafka;

import java.time.LocalDateTime;

public record ReadingMessage(
        Long deviceId,
        Long organizationId,
        Double value,
        LocalDateTime recordedAt
) {
    public static ReadingMessage of(Long deviceId, Long organizationId, Double value, LocalDateTime recordedAt) {
        return new ReadingMessage(deviceId, organizationId, value, recordedAt);
    }
}