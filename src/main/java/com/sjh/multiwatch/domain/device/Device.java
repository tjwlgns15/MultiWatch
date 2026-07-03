package com.sjh.multiwatch.domain.device;

import com.sjh.multiwatch.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Getter
@NoArgsConstructor
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "orgId", type = Long.class))
@Filter(name = "tenantFilter", condition = "organization_id = :orgId")
public class Device extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(nullable = false, unique = true)
    private String deviceKey;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @Column(name = "last_reading_at")
    private LocalDateTime lastReadingAt;

    private Device(Long organizationId, String deviceKey, String name, DeviceType deviceType) {
        this.organizationId = organizationId;
        this.deviceKey = deviceKey;
        this.name = name;
        this.deviceType = deviceType;
        this.status = DeviceStatus.OFFLINE;
    }

    public static Device register(Long organizationId, String deviceKey, String name, DeviceType deviceType) {
        return new Device(organizationId, deviceKey, name, deviceType);
    }

    /**
     * 리딩 수신 시 호출 - 온라인 전환 + 마지막 수신 시각 갱신을 하나의 도메인 행위로 묶음.
     */
    public void receiveReading(LocalDateTime recordedAt) {
        this.status = DeviceStatus.ONLINE;
        this.lastReadingAt = recordedAt;
    }

    public void markOnline() {
        this.status = DeviceStatus.ONLINE;
    }

    public void markOffline() {
        this.status = DeviceStatus.OFFLINE;
    }
}