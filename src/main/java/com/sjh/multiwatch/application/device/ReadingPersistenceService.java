package com.sjh.multiwatch.application.device;

import com.sjh.multiwatch.domain.device.DeviceReading;
import com.sjh.multiwatch.domain.device.DeviceReadingRepository;
import com.sjh.multiwatch.infrastructure.kafka.ReadingMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReadingPersistenceService {

    private final DeviceReadingRepository deviceReadingRepository;

    public void save(ReadingMessage message) {
        DeviceReading reading = DeviceReading.record(
                message.deviceId(), message.organizationId(), message.value(), message.recordedAt()
        );
        deviceReadingRepository.save(reading);
    }
}
