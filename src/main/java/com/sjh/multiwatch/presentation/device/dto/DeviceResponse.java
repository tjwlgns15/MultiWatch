package com.sjh.multiwatch.presentation.device.dto;

import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceStatus;
import com.sjh.multiwatch.domain.device.DeviceType;

public record DeviceResponse(
        Long id,
        String deviceKey,
        String name,
        DeviceType deviceType,
        DeviceStatus status
) {

    public static DeviceResponse from(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getDeviceKey(),
                device.getName(),
                device.getDeviceType(),
                device.getStatus()
        );
    }
}
