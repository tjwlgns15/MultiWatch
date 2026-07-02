package com.sjh.multiwatch.presentation.device.dto;

import com.sjh.multiwatch.domain.device.DeviceType;

public record RegisterDeviceRequest(
        String deviceKey,
        String name,
        DeviceType deviceType
) {}
