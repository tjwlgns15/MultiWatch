package com.sjh.multiwatch.application.device;

import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceReading;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.domain.device.DeviceReadingRepository;
import com.sjh.multiwatch.infrastructure.exception.CustomException;
import com.sjh.multiwatch.infrastructure.security.aop.TenantScoped;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjh.multiwatch.infrastructure.exception.ErrorCode.DEVICE_NOT_FOUND;

@Service
@TenantScoped
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadingQueryService {

    private final DeviceRepository deviceRepository;
    private final DeviceReadingRepository deviceReadingRepository;

    public List<DeviceReading> getRecentReadings(Long deviceId, Long organizationId, int limit) {
        Device device = deviceRepository.findByIdAndOrganizationId(deviceId, organizationId)
                .orElseThrow(() -> new CustomException(DEVICE_NOT_FOUND));

        return deviceReadingRepository.findByDeviceIdOrderByRecordedAtDesc(device.getId(), PageRequest.of(0, limit));
    }
}