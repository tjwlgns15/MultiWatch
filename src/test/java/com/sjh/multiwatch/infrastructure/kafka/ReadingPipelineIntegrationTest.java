package com.sjh.multiwatch.infrastructure.kafka;

import com.sjh.multiwatch.domain.alert.Alert;
import com.sjh.multiwatch.domain.alert.AlertRepository;
import com.sjh.multiwatch.domain.alert.AlertRule;
import com.sjh.multiwatch.domain.alert.AlertRuleRepository;
import com.sjh.multiwatch.domain.alert.Comparator;
import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceReadingRepository;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.domain.device.DeviceStatus;
import com.sjh.multiwatch.domain.device.DeviceType;
import com.sjh.multiwatch.domain.organization.Organization;
import com.sjh.multiwatch.domain.organization.OrganizationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * 내장 Kafka 브로커 사용 - 별도 로컬 Kafka 불필요.
 * MySQL은 실제 로컬 인스턴스 필요 (localhost:3308)
 */
@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 1, topics = { KafkaTopics.DEVICE_READINGS })
class ReadingPipelineIntegrationTest {

    @Autowired KafkaTemplate<String, ReadingMessage> kafkaTemplate;
    @Autowired KafkaListenerEndpointRegistry endpointRegistry;
    @Autowired EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired OrganizationRepository organizationRepository;
    @Autowired DeviceRepository deviceRepository;
    @Autowired DeviceReadingRepository deviceReadingRepository;
    @Autowired AlertRuleRepository alertRuleRepository;
    @Autowired AlertRepository alertRepository;

    private Organization organization;
    private Device device;

    @BeforeEach
    void waitForConsumerAssignment() {
        // 컨슈머가 파티션을 실제로 할당받을 때까지 대기 - 첫 메시지 유실 방지
        for (MessageListenerContainer container : endpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @BeforeEach
    void setUp() {
        organization = organizationRepository.save(Organization.register("테스트사"));
        device = deviceRepository.save(
                Device.register(organization.getId(), "device-test", "테스트센서", DeviceType.TEMPERATURE)
        );
    }

    @AfterEach
    void tearDown() {
        alertRepository.deleteAll();
        alertRuleRepository.deleteAll();
        deviceReadingRepository.deleteAll();
        deviceRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    @DisplayName("발행된 리딩 메시지가 저장되고 디바이스가 온라인으로 전환된다")
    void readingMessageIsPersistedAndDeviceGoesOnline() {
        LocalDateTime recordedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        ReadingMessage message = new ReadingMessage(device.getId(), organization.getId(), 20.0, recordedAt);

        kafkaTemplate.send(KafkaTopics.DEVICE_READINGS, organization.getId().toString(), message);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            assertThat(deviceReadingRepository.findByDeviceIdOrderByRecordedAtDesc(device.getId(), PageRequest.of(0, 10)))
                    .hasSize(1);

            Device updated = deviceRepository.findById(device.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(DeviceStatus.ONLINE);
            assertThat(updated.getLastReadingAt()).isEqualTo(recordedAt);
        });
    }

    @Test
    @DisplayName("임계치를 위반하는 리딩이 들어오면 Alert가 자동으로 발생한다")
    void violatingReadingRaisesAlert() {
        AlertRule rule = alertRuleRepository.save(AlertRule.create(device.getId(), 30.0, Comparator.GT));
        ReadingMessage violatingMessage = new ReadingMessage(device.getId(), organization.getId(), 45.0, LocalDateTime.now());

        kafkaTemplate.send(KafkaTopics.DEVICE_READINGS, organization.getId().toString(), violatingMessage);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            List<Alert> alerts = alertRepository.findAll();
            assertThat(alerts).hasSize(1);
            assertThat(alerts.get(0).getAlertRuleId()).isEqualTo(rule.getId());
            assertThat(alerts.get(0).getTriggeredValue()).isEqualTo(45.0);
        });
    }

    @Test
    @DisplayName("임계치를 위반하지 않는 리딩은 Alert를 발생시키지 않는다")
    void nonViolatingReadingDoesNotRaiseAlert() {
        alertRuleRepository.save(AlertRule.create(device.getId(), 30.0, Comparator.GT));
        ReadingMessage safeMessage = new ReadingMessage(device.getId(), organization.getId(), 20.0, LocalDateTime.now());

        kafkaTemplate.send(KafkaTopics.DEVICE_READINGS, organization.getId().toString(), safeMessage);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(deviceReadingRepository.findByDeviceIdOrderByRecordedAtDesc(device.getId(), PageRequest.of(0, 10)))
                        .hasSize(1)
        );

        assertThat(alertRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("비활성화된 규칙은 임계치를 위반해도 Alert를 발생시키지 않는다")
    void disabledRuleDoesNotRaiseAlert() {
        AlertRule rule = AlertRule.create(device.getId(), 30.0, Comparator.GT);
        rule.disable();
        alertRuleRepository.save(rule);

        ReadingMessage violatingMessage = new ReadingMessage(device.getId(), organization.getId(), 45.0, LocalDateTime.now());
        kafkaTemplate.send(KafkaTopics.DEVICE_READINGS, organization.getId().toString(), violatingMessage);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(deviceReadingRepository.findByDeviceIdOrderByRecordedAtDesc(device.getId(), PageRequest.of(0, 10)))
                        .hasSize(1)
        );

        assertThat(alertRepository.findAll()).isEmpty();
    }
}