package com.sjh.multiwatch.infrastructure.scheduler;

import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.domain.device.DeviceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceOfflineScheduler {

    private static final Duration STALE_THRESHOLD = Duration.ofMinutes(2);

    private final DeviceRepository deviceRepository;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void markStaleDevicesOffline() {
        LocalDateTime threshold = LocalDateTime.now().minus(STALE_THRESHOLD);
        List<Device> staleDevices = deviceRepository.findByStatusAndLastReadingAtBefore(DeviceStatus.ONLINE, threshold);

        staleDevices.forEach(Device::markOffline);

        if (!staleDevices.isEmpty()) {
            log.info("오프라인 전환된 디바이스 수: {}", staleDevices.size());
        }
    }
}