package com.sjh.multiwatch.application.device;

import com.sjh.multiwatch.domain.device.Device;
import com.sjh.multiwatch.domain.device.DeviceRepository;
import com.sjh.multiwatch.infrastructure.exception.CustomException;
import com.sjh.multiwatch.infrastructure.kafka.ReadingKafkaProducer;
import com.sjh.multiwatch.infrastructure.security.aop.TenantScoped;
import com.sjh.multiwatch.presentation.api.device.dto.IngestReadingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjh.multiwatch.infrastructure.exception.ErrorCode.DEVICE_NOT_FOUND;

@Service
@TenantScoped
@RequiredArgsConstructor
public class ReadingIngestService {

    private final DeviceRepository deviceRepository;
    private final ReadingKafkaProducer readingKafkaProducer;

    @Transactional(readOnly = true)
    public void ingest(List<IngestReadingRequest> requests) {
        for (IngestReadingRequest request : requests) {
            publish(request);
        }
    }

    private void publish(IngestReadingRequest request) {
        Device device = deviceRepository.findByDeviceKey(request.deviceKey())
                .orElseThrow(() -> new CustomException(DEVICE_NOT_FOUND));

        readingKafkaProducer.send(device.getId(), device.getOrganizationId(), request.value(), request.recordedAt());
    }
}
