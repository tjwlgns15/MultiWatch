package com.sjh.multiwatch.domain.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceReadingRepository extends JpaRepository<DeviceReading, Long> {
}
