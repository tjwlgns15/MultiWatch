package com.sjh.multiwatch.infrastructure.kafka;

import com.sjh.multiwatch.application.alert.AlertEvaluationService;
import com.sjh.multiwatch.application.device.ReadingPersistenceService;
import com.sjh.multiwatch.infrastructure.websocket.ReadingBroadcastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReadingKafkaConsumer {

    private final ReadingPersistenceService readingPersistenceService;
    private final AlertEvaluationService alertEvaluationService;
    private final ReadingBroadcastService readingBroadcastService;


    @KafkaListener(topics = KafkaTopics.DEVICE_READINGS, groupId = "monitoring-reading-consumer")
    public void consume(ReadingMessage message) {
        try {
            readingPersistenceService.save(message);
            alertEvaluationService.evaluate(message);
            readingBroadcastService.broadcast(message);
        } catch (Exception e) {
            log.error("Reading 처리 실패 - deviceId: {}, error: {}", message.deviceId(), e.getMessage(), e);
        }
    }
}
