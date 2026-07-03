package com.sjh.multiwatch.domain.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByDeviceIdAndEnabledTrue(Long deviceId);

    List<AlertRule> findByDeviceId(Long deviceId);
}
