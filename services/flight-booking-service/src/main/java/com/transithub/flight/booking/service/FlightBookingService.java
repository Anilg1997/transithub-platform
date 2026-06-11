package com.transithub.flight.booking.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FlightBookingService {
    private final NamedParameterJdbcTemplate jdbc;
    public FlightBookingService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Passenger(String name, int age, String gender, String idType, String idNumber,
                            String seatNumber, String baggageOption, String mealPreference) {}
    public record FlightBooking(UUID id, String bookingRef, String pnr, UUID flightId, String status,
                                List<Passenger> passengers, List<String> seatNumbers, double totalFare, String bookedAt) {}

    public List<FlightBooking> getMyFlightBookings(UUID userId) {
        return jdbc.query(
            "SELECT id, booking_ref, pnr, flight_id, status, total_fare, booked_at FROM flight_schema.flight_bookings WHERE user_id = :userId ORDER BY booked_at DESC",
            Map.of("userId", userId),
            (rs, row) -> new FlightBooking(rs.getObject("id", UUID.class), rs.getString("booking_ref"),
                rs.getString("pnr"), rs.getObject("flight_id", UUID.class), rs.getString("status"),
                List.of(), List.of(), rs.getDouble("total_fare"), rs.getString("booked_at")));
    }

    public FlightBooking getFlightBooking(String bookingRef) {
        return jdbc.queryForObject(
            "SELECT id, booking_ref, pnr, flight_id, status, total_fare, booked_at FROM flight_schema.flight_bookings WHERE booking_ref = :ref",
            Map.of("ref", bookingRef),
            (rs, row) -> new FlightBooking(rs.getObject("id", UUID.class), rs.getString("booking_ref"),
                rs.getString("pnr"), rs.getObject("flight_id", UUID.class), rs.getString("status"),
                List.of(), List.of(), rs.getDouble("total_fare"), rs.getString("booked_at")));
    }

    @Transactional
    public FlightBooking bookFlight(UUID userId, String flightId, String date, String cabinClass,
                                     List<Map<String, Object>> passengers, List<String> seatNumbers) {
        String bookingRef = "FL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String pnr = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO flight_schema.flight_bookings (id, user_id, flight_id, booking_ref, pnr, status, total_fare, booked_at) " +
            "VALUES (:id, :userId, :flightId, :ref, :pnr, 'CONFIRMED', 5000.0, NOW())",
            Map.of("id", id, "userId", userId, "flightId", UUID.fromString(flightId), "ref", bookingRef, "pnr", pnr));
        return new FlightBooking(id, bookingRef, pnr, UUID.fromString(flightId), "CONFIRMED", List.of(), seatNumbers != null ? seatNumbers : List.of(), 5000.0, java.time.LocalDateTime.now().toString());
    }

    @Transactional
    public FlightBooking cancelFlight(String bookingRef) {
        jdbc.update("UPDATE flight_schema.flight_bookings SET status = 'CANCELLED' WHERE booking_ref = :ref",
            Map.of("ref", bookingRef));
        return getFlightBooking(bookingRef);
    }

    @Transactional
    public FlightBooking checkIn(String bookingRef, String pnr) {
        jdbc.update("UPDATE flight_schema.flight_bookings SET status = 'CHECKED_IN' WHERE booking_ref = :ref AND pnr = :pnr",
            Map.of("ref", bookingRef, "pnr", pnr));
        return getFlightBooking(bookingRef);
    }
}
