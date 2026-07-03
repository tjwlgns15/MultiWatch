package com.sjh.multiwatch.application.device;

import com.sjh.multiwatch.domain.device.DeviceReading;
import com.sjh.multiwatch.domain.device.DeviceReadingRepository;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.infrastructure.kafka.ReadingMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReadingPersistenceService {

    private final DeviceReadingRepository deviceReadingRepository;
    private final DeviceRepository deviceRepository;

    @Transactional
    public void save(ReadingMessage message) {
        DeviceReading reading = DeviceReading.record(
                message.deviceId(), message.organizationId(), message.value(), message.recordedAt()
        );
        deviceReadingRepository.save(reading);

        deviceRepository.findById(message.deviceId())
                .ifPresent(device -> device.receiveReading(message.recordedAt()));
    }
}
