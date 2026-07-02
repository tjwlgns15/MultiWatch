package com.sjh.multiwatch.presentation.device.dto;

import java.time.LocalDateTime;

public record IngestReadingRequest (
        String deviceKey,
        Double value,
        LocalDateTime recordedAt
){}
