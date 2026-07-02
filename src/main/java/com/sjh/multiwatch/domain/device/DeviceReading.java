package com.sjh.multiwatch.domain.device;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_readings")
@Getter
@NoArgsConstructor
public class DeviceReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId; // 비정규화 - 필터/인덱스 성능용

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    private DeviceReading(Long deviceId, Long organizationId, Double value, LocalDateTime recordedAt) {
        this.deviceId = deviceId;
        this.organizationId = organizationId;
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public static DeviceReading record(Long deviceId, Long organizationId, Double value, LocalDateTime recordedAt) {
        return new DeviceReading(deviceId, organizationId, value, recordedAt);
    }
}
