package com.sjh.multiwatch.application.device;

import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.infrastructure.exception.CustomException;
import com.sjh.multiwatch.infrastructure.security.aop.TenantScoped;
import com.sjh.multiwatch.presentation.api.device.dto.RegisterDeviceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjh.multiwatch.infrastructure.exception.ErrorCode.DUPLICATE_DEVICE_KEY;

@TenantScoped
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;


    @Transactional
    public Long registerDevice(RegisterDeviceRequest request, Long organizationId) {
        validateDeviceKeyNotDuplicated(request.deviceKey());

        Device device = Device.register(
                organizationId,
                request.deviceKey(),
                request.name(),
                request.deviceType()
        );

        return deviceRepository.save(device).getId();
    }

    public List<Device> getDevices() {
        // tenantFilter가 이미 활성화되어 있으므로 별도 where 조건 없이 자동 격리됨
        return deviceRepository.findAll();
    }

    private void validateDeviceKeyNotDuplicated(String deviceKey) {
        if (deviceRepository.existsByDeviceKey(deviceKey)) {
            throw new CustomException(DUPLICATE_DEVICE_KEY);
        }
    }

}
