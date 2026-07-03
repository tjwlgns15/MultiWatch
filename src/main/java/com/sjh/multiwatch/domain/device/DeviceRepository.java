package com.sjh.multiwatch.domain.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    boolean existsByDeviceKey(String deviceKey);
    Optional<Device> findByDeviceKey(String deviceKey);
    Optional<Device> findByIdAndOrganizationId(Long id, Long organizationId);
    List<Device> findByStatusAndLastReadingAtBefore(DeviceStatus status, LocalDateTime threshold);

}
