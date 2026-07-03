package com.sjh.multiwatch.presentation.api.device.dto;

import com.sjh.multiwatch.domain.device.DeviceReading;

import java.time.LocalDateTime;

public record DeviceReadingResponse (
        Long id,
        Double value,
        LocalDateTime recordedAt
) {
    public static DeviceReadingResponse from(DeviceReading reading) {
        return new DeviceReadingResponse(reading.getId(), reading.getValue(), reading.getRecordedAt());
    }
}
