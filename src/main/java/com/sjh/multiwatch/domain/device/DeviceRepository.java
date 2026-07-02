package com.sjh.multiwatch.domain.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    boolean existsByDeviceKey(String deviceKey);
    Optional<Device> findByDeviceKey(String deviceKey);
}
