package com.sjh.multiwatch.presentation.api.device.dto;

import java.time.LocalDateTime;

public record IngestReadingRequest (
        String deviceKey,
        Double value,
        LocalDateTime recordedAt
){}
