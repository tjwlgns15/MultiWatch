package com.sjh.multiwatch.domain.device;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceTest {

    @Test
    @DisplayName("등록 직후 디바이스는 OFFLINE 상태다")
    void register_offlineByDefault() {
        Device device = Device.register(1L, "device-key", "센서A", DeviceType.TEMPERATURE);

        assertThat(device.getStatus()).isEqualTo(DeviceStatus.OFFLINE);
        assertThat(device.getLastReadingAt()).isNull();
    }

    @Test
    @DisplayName("리딩을 수신하면 ONLINE으로 전환되고 마지막 수신 시각이 갱신된다")
    void receiveReading_marksOnlineAndUpdatesTimestamp() {
        Device device = Device.register(1L, "device-key", "센서A", DeviceType.TEMPERATURE);
        LocalDateTime recordedAt = LocalDateTime.of(2026, 7, 2, 15, 0);

        device.receiveReading(recordedAt);

        assertThat(device.getStatus()).isEqualTo(DeviceStatus.ONLINE);
        assertThat(device.getLastReadingAt()).isEqualTo(recordedAt);
    }

    @Test
    @DisplayName("markOffline 호출 시 상태만 변경되고 마지막 수신 시각은 유지된다")
    void markOffline_keepsLastReadingAt() {
        Device device = Device.register(1L, "device-key", "센서A", DeviceType.TEMPERATURE);
        LocalDateTime recordedAt = LocalDateTime.of(2026, 7, 2, 15, 0);
        device.receiveReading(recordedAt);

        device.markOffline();

        assertThat(device.getStatus()).isEqualTo(DeviceStatus.OFFLINE);
        assertThat(device.getLastReadingAt()).isEqualTo(recordedAt);
    }
}