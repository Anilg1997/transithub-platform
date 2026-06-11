package com.transithub.train.booking.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TrainBookingService {
    private final NamedParameterJdbcTemplate jdbc;
    public TrainBookingService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Passenger(String name, int age, String gender, String idType, String idNumber) {}
    public record TrainBooking(UUID id, String bookingRef, String pnr, UUID trainId, String status,
                               List<Passenger> passengers, String coach, List<String> berths,
                               double totalFare, String bookedAt, Integer waitlistNumber) {}

    public List<TrainBooking> getMyTrainBookings(UUID userId) {
        return jdbc.query(
            "SELECT id, booking_ref, pnr, train_id, status, coach, berths, total_fare, booked_at, waitlist_number " +
            "FROM train_schema.train_bookings WHERE user_id = :userId ORDER BY booked_at DESC",
            Map.of("userId", userId),
            (rs, row) -> new TrainBooking(rs.getObject("id", UUID.class), rs.getString("booking_ref"),
                rs.getString("pnr"), rs.getObject("train_id", UUID.class), rs.getString("status"),
                List.of(), rs.getString("coach"), List.of(rs.getString("berths").split(",")),
                rs.getDouble("total_fare"), rs.getString("booked_at"),
                rs.getObject("waitlist_number") != null ? rs.getInt("waitlist_number") : null));
    }

    public TrainBooking getTrainBooking(String bookingRef) {
        return jdbc.queryForObject(
            "SELECT id, booking_ref, pnr, train_id, status, coach, berths, total_fare, booked_at, waitlist_number " +
            "FROM train_schema.train_bookings WHERE booking_ref = :ref",
            Map.of("ref", bookingRef),
            (rs, row) -> new TrainBooking(rs.getObject("id", UUID.class), rs.getString("booking_ref"),
                rs.getString("pnr"), rs.getObject("train_id", UUID.class), rs.getString("status"),
                List.of(), rs.getString("coach"), List.of(rs.getString("berths").split(",")),
                rs.getDouble("total_fare"), rs.getString("booked_at"),
                rs.getObject("waitlist_number") != null ? rs.getInt("waitlist_number") : null));
    }

    @Transactional
    public TrainBooking bookTrain(UUID userId, String trainId, String date, String coachType,
                                   String quota, List<Map<String, Object>> passengers, List<String> berthPreferences) {
        String bookingRef = "TR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String pnr = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO train_schema.train_bookings (id, user_id, train_id, booking_ref, pnr, status, coach, berths, total_fare, booked_at) " +
            "VALUES (:id, :userId, :trainId, :ref, :pnr, 'CONFIRMED', :coach, :berths, 1800.0, NOW())",
            Map.of("id", id, "userId", userId, "trainId", UUID.fromString(trainId), "ref", bookingRef,
                "pnr", pnr, "coach", coachType, "berths", berthPreferences != null ? String.join(",", berthPreferences) : ""));
        return new TrainBooking(id, bookingRef, pnr, UUID.fromString(trainId), "CONFIRMED",
            List.of(), coachType, berthPreferences != null ? berthPreferences : List.of(),
            1800.0, java.time.LocalDateTime.now().toString(), null);
    }

    @Transactional
    public TrainBooking cancelTrain(String bookingRef) {
        jdbc.update("UPDATE train_schema.train_bookings SET status = 'CANCELLED' WHERE booking_ref = :ref", Map.of("ref", bookingRef));
        return getTrainBooking(bookingRef);
    }

    public TrainBooking checkPnrStatus(String pnr) {
        return jdbc.queryForObject(
            "SELECT id, booking_ref, pnr, train_id, status, coach, berths, total_fare, booked_at, waitlist_number " +
            "FROM train_schema.train_bookings WHERE pnr = :pnr",
            Map.of("pnr", pnr),
            (rs, row) -> new TrainBooking(rs.getObject("id", UUID.class), rs.getString("booking_ref"),
                rs.getString("pnr"), rs.getObject("train_id", UUID.class), rs.getString("status"),
                List.of(), rs.getString("coach"), List.of(rs.getString("berths").split(",")),
                rs.getDouble("total_fare"), rs.getString("booked_at"),
                rs.getObject("waitlist_number") != null ? rs.getInt("waitlist_number") : null));
    }
}
