package com.sjh.multiwatch.infrastructure.kafka;

import com.sjh.multiwatch.domain.device.DeviceReading;
import com.sjh.multiwatch.domain.device.DeviceReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReadingKafkaConsumer {

    private final DeviceReadingRepository readingRepository;


    @KafkaListener(topics = KafkaTopics.DEVICE_READINGS, groupId = "monitoring-reading-consumer")
    public void consume(ReadingMessage message) {
        try {
            DeviceReading record = DeviceReading.record(message.deviceId(), message.organizationId(), message.value(), message.recordedAt());
            readingRepository.save(record);
        } catch (Exception e) {
            log.error("Reading 처리 실패 - deviceId: {}, error: {}", message.deviceId(), e.getMessage(), e);
        }
    }
}
