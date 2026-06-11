package com.transithub.booking.aggregator.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookingAggregatorService {
    private final NamedParameterJdbcTemplate jdbc;
    public BookingAggregatorService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record TripSegment(String mode, String from, String to, String departure, String arrival, String operator, double fare, int availableOptions) {}
    public record TripOption(List<TripSegment> segments, double totalFare, int totalDuration, int transfers) {}
    public record BookingSegment(String mode, String bookingRef, String status, double fare) {}
    public record MultiModalBooking(UUID id, String combinedRef, List<BookingSegment> segments, double totalFare, String status, String createdAt) {}

    public List<TripOption> planTrip(List<Map<String, Object>> segments, Integer pax) {
        var options = new java.util.ArrayList<TripOption>();
        var segList = new java.util.ArrayList<TripSegment>();
        double totalFare = 0;
        int totalDuration = 0;
        for (var seg : segments) {
            String mode = (String) seg.get("mode");
            String from = (String) seg.get("from");
            String to = (String) seg.get("to");
            String date = (String) seg.get("date");
            segList.add(new TripSegment(mode, from, to, date, date, "Various", 2500.0, 5));
            totalFare += 2500.0;
            totalDuration += 360;
        }
        options.add(new TripOption(segList, totalFare, totalDuration, segments.size() - 1));
        return options;
    }

    public List<MultiModalBooking> getMyBookings(UUID userId) {
        return jdbc.query(
            "SELECT id, combined_ref, segments, total_fare, status, created_at FROM booking_schema.multi_modal_bookings WHERE user_id = :userId ORDER BY created_at DESC",
            Map.of("userId", userId),
            (rs, row) -> new MultiModalBooking(rs.getObject("id", UUID.class), rs.getString("combined_ref"),
                List.of(new BookingSegment("FLIGHT", "", "CONFIRMED", 2500.0)),
                rs.getDouble("total_fare"), rs.getString("status"), rs.getString("created_at")));
    }

    @Transactional
    public MultiModalBooking createMultiModalBooking(UUID userId, List<Map<String, Object>> segments, List<Map<String, Object>> passengers) {
        String combinedRef = "MM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO booking_schema.multi_modal_bookings (id, user_id, combined_ref, segments, total_fare, status, created_at) " +
            "VALUES (:id, :userId, :ref, :segments, 5000.0, 'CONFIRMED', NOW())",
            Map.of("id", id, "userId", userId, "ref", combinedRef, "segments", combinedRef));
        return new MultiModalBooking(id, combinedRef,
            List.of(new BookingSegment("FLIGHT", "FL-001", "CONFIRMED", 2500.0),
                    new BookingSegment("TRAIN", "TR-001", "CONFIRMED", 2500.0)),
            5000.0, "CONFIRMED", java.time.LocalDateTime.now().toString());
    }
}
