package com.transithub.train.availability.service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class TrainAvailabilityService {
    private final StringRedisTemplate redis;
    private static final String AVAIL_PREFIX = "th:train:avail:";

    public TrainAvailabilityService(StringRedisTemplate redis) { this.redis = redis; }

    public record Availability(String trainId, String date, String coachType, int availableBerths, int waitlistCount) {}

    public Availability getAvailability(String trainId, String date, String coachType) {
        String key = AVAIL_PREFIX + trainId + ":" + date + ":" + coachType;
        var hash = redis.opsForHash().entries(key);
        if (hash.isEmpty()) {
            return new Availability(trainId, date, coachType, 0, 0);
        }
        return new Availability(trainId, date, coachType,
            Integer.parseInt((String) hash.getOrDefault("availableBerths", "0")),
            Integer.parseInt((String) hash.getOrDefault("waitlistCount", "0")));
    }

    public void publishAvailabilityUpdate(String trainId, String date, String coachType, int availableBerths, int waitlistCount) {
        String key = AVAIL_PREFIX + trainId + ":" + date + ":" + coachType;
        redis.opsForHash().putAll(key, Map.of(
            "availableBerths", String.valueOf(availableBerths),
            "waitlistCount", String.valueOf(waitlistCount)));
    }
}
