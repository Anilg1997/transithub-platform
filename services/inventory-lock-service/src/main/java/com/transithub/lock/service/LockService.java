package com.transithub.lock.service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {
    private final StringRedisTemplate redis;
    private static final Duration LOCK_TTL = Duration.ofMinutes(10);
    private static final String LOCK_PREFIX = "th:lock:";

    public LockService(StringRedisTemplate redis) { this.redis = redis; }

    public record LockResult(String lockToken, String inventoryType, String itemId, boolean success) {}

    public LockResult lockSeat(String inventoryType, String itemId, String seatId, String userId) {
        String lockKey = LOCK_PREFIX + inventoryType + ":" + itemId + ":" + seatId;
        String lockToken = UUID.randomUUID().toString();
        Boolean acquired = redis.opsForValue().setIfAbsent(lockKey, userId + ":" + lockToken, LOCK_TTL);
        if (Boolean.TRUE.equals(acquired)) {
            // Store the lock info for the user
            redis.opsForSet().add(LOCK_PREFIX + "user:" + userId, lockKey);
            redis.expire(LOCK_PREFIX + "user:" + userId, LOCK_TTL);
            return new LockResult(lockToken, inventoryType, itemId, true);
        }
        return new LockResult(null, inventoryType, itemId, false);
    }

    public boolean releaseSeat(String inventoryType, String itemId, String seatId, String userId, String lockToken) {
        String lockKey = LOCK_PREFIX + inventoryType + ":" + itemId + ":" + seatId;
        String value = redis.opsForValue().get(lockKey);
        if (value != null && value.equals(userId + ":" + lockToken)) {
            redis.delete(lockKey);
            redis.opsForSet().remove(LOCK_PREFIX + "user:" + userId, lockKey);
            return true;
        }
        return false;
    }

    public boolean isSeatLocked(String inventoryType, String itemId, String seatId) {
        return Boolean.TRUE.equals(redis.hasKey(LOCK_PREFIX + inventoryType + ":" + itemId + ":" + seatId));
    }

    public boolean extendLock(String inventoryType, String itemId, String seatId, String userId, String lockToken) {
        String lockKey = LOCK_PREFIX + inventoryType + ":" + itemId + ":" + seatId;
        String value = redis.opsForValue().get(lockKey);
        if (value != null && value.equals(userId + ":" + lockToken)) {
            redis.expire(lockKey, LOCK_TTL.getSeconds(), TimeUnit.SECONDS);
            return true;
        }
        return false;
    }
}
