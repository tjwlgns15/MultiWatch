package com.sjh.multiwatch.application.device;

import com.sjh.multiwatch.domain.device.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReadingIngestService {

    private final DeviceRepository deviceRepository;

}
