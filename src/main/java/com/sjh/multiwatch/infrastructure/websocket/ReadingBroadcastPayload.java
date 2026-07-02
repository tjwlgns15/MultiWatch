package com.sjh.multiwatch.infrastructure.websocket;

import com.sjh.multiwatch.infrastructure.kafka.ReadingMessage;

import java.time.LocalDateTime;

public record ReadingBroadcastPayload(
        Long deviceId,
        Double value,
        LocalDateTime recordedAt
) {

    public static ReadingBroadcastPayload from(ReadingMessage message) {
        return new ReadingBroadcastPayload(message.deviceId(), message.value(), message.recordedAt());
    }
}
