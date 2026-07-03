package com.sjh.multiwatch.domain.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByDeviceIdAndEnabledTrue(Long deviceId);

    List<AlertRule> findByDeviceId(Long deviceId);

    /**
     * AlertRule은 organizationId를 직접 갖지 않으므로(deviceId만 보유),
     * Device와 조인해서 소유권을 검증한다.
     */
    @Query("""
            SELECT ar FROM AlertRule ar
            JOIN Device d ON d.id = ar.deviceId
            WHERE ar.id = :ruleId AND d.organizationId = :organizationId
            """)
    Optional<AlertRule> findByIdAndOrganizationId(@Param("ruleId") Long ruleId,
                                                  @Param("organizationId") Long organizationId);
}
