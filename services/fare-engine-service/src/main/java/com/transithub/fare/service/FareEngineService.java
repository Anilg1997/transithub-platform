package com.transithub.fare.service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FareEngineService {
    private final NamedParameterJdbcTemplate jdbc;
    private final StringRedisTemplate redis;
    private static final String FARE_CACHE = "th:fare:";

    public FareEngineService(NamedParameterJdbcTemplate jdbc, StringRedisTemplate redis) {
        this.jdbc = jdbc;
        this.redis = redis;
    }

    public record FareCalculation(double baseFare, double taxes, double gst, double convenienceFee,
                                  double totalFare, double demandMultiplier) {}
    public record RefundPolicy(double refundPercentage, double cancellationFee, int hoursBeforeDeparture, boolean isRefundable) {}

    public FareCalculation calculateFare(String inventoryType, String itemId, String cabin, Integer pax) {
        int passengers = pax != null ? pax : 1;
        String cacheKey = FARE_CACHE + inventoryType + ":" + itemId + ":" + (cabin != null ? cabin : "DEFAULT") + ":" + passengers;
        var cached = redis.opsForValue().get(cacheKey);
        if (cached != null && cached.contains("|")) {
            String[] parts = cached.split("\\|");
            return new FareCalculation(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                Double.parseDouble(parts[4]), Double.parseDouble(parts[5]));
        }

        double baseFare = switch (inventoryType.toUpperCase()) {
            case "FLIGHT" -> 5000.0 * passengers;
            case "BUS" -> 1500.0 * passengers;
            case "TRAIN" -> 1200.0 * passengers;
            default -> 3000.0 * passengers;
        };
        double taxes = baseFare * 0.12;
        double gst = baseFare * 0.05;
        double convenienceFee = baseFare * 0.02;
        double demandMultiplier = 1.0;
        double totalFare = (baseFare + taxes + gst + convenienceFee) * demandMultiplier;

        redis.opsForValue().set(cacheKey,
            baseFare + "|" + taxes + "|" + gst + "|" + convenienceFee + "|" + totalFare + "|" + demandMultiplier,
            java.time.Duration.ofMinutes(15));

        return new FareCalculation(baseFare, taxes, gst, convenienceFee, totalFare, demandMultiplier);
    }

    public RefundPolicy getRefundPolicy(String bookingRef, int hoursBeforeDeparture) {
        return switch (hoursBeforeDeparture) {
            case int h when h >= 48 -> new RefundPolicy(80.0, 200.0, hoursBeforeDeparture, true);
            case int h when h >= 24 -> new RefundPolicy(50.0, 500.0, hoursBeforeDeparture, true);
            case int h when h >= 12 -> new RefundPolicy(25.0, 750.0, hoursBeforeDeparture, true);
            default -> new RefundPolicy(0.0, 0.0, hoursBeforeDeparture, false);
        };
    }
}
