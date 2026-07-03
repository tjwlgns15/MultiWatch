package com.sjh.multiwatch.infrastructure.redis.alert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjh.multiwatch.domain.alert.AlertRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AlertRuleCacheRepository {

    private static final String KEY_PREFIX = "alert-rules:device:";
    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, String> redisTemplate;
    private final AlertRuleRepository alertRuleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Cache-Aside 조회.
     * 1) Redis 히트 → 즉시 반환
     * 2) 미스 또는 Redis 장애 → DB 조회 후 캐시 적재
     *
     * 조회/저장 실패를 각각 try-catch로 감싼 이유:
     * Redis 장애가 알림 평가 파이프라인 전체를 막아서는 안 되기 때문 (가용성 우선).
     */
    public List<AlertRuleCacheEntry> findByDeviceId(Long deviceId) {
        String key = key(deviceId);

        List<AlertRuleCacheEntry> cached = readFromCache(key);
        if (cached != null) {
            return cached;
        }

        List<AlertRuleCacheEntry> fromDb = loadFromDb(deviceId);
        writeToCache(key, fromDb);
        return fromDb;
    }

    /**
     * AlertRule 등록/비활성화 시 호출 — 다음 조회에서 DB 최신값이 다시 캐싱되도록 무효화.
     */
    public void evict(Long deviceId) {
        try {
            redisTemplate.delete(key(deviceId));
        } catch (Exception e) {
            log.warn("[AlertRuleCache] 캐시 삭제 실패 deviceId={}, error={}", deviceId, e.getMessage());
        }
    }

    private List<AlertRuleCacheEntry> readFromCache(String key) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<List<AlertRuleCacheEntry>>() {});
        } catch (Exception e) {
            log.warn("[AlertRuleCache] 캐시 조회 실패 key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    private void writeToCache(String key, List<AlertRuleCacheEntry> entries) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(entries), TTL);
        } catch (Exception e) {
            log.warn("[AlertRuleCache] 캐시 저장 실패 key={}, error={}", key, e.getMessage());
        }
    }

    private List<AlertRuleCacheEntry> loadFromDb(Long deviceId) {
        return alertRuleRepository.findByDeviceIdAndEnabledTrue(deviceId).stream()
                .map(AlertRuleCacheEntry::from)
                .toList();
    }

    private String key(Long deviceId) {
        return KEY_PREFIX + deviceId;
    }
}