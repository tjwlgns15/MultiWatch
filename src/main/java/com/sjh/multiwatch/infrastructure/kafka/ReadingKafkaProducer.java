package com.sjh.multiwatch.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReadingKafkaProducer {

    private final KafkaTemplate<String, ReadingMessage> kafkaTemplate;

    public void send(Long deviceId, Long organizationId, Double value, LocalDateTime recordedAt) {
        ReadingMessage message = ReadingMessage.of(deviceId, organizationId, value, recordedAt);

        // organizationId를 key로 사용 → 같은 조직 데이터는 같은 파티션에서 순서 보장
        kafkaTemplate.send(KafkaTopics.DEVICE_READINGS, organizationId.toString(), message);
    }
}
