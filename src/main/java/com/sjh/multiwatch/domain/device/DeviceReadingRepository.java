package com.sjh.multiwatch.domain.device;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceReadingRepository extends JpaRepository<DeviceReading, Long> {
    List<DeviceReading> findByDeviceIdOrderByRecordedAtDesc(Long deviceId, Pageable pageable);
}
