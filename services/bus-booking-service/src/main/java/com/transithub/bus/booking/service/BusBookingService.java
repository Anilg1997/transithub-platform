package com.transithub.bus.booking.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BusBookingService {
    private final NamedParameterJdbcTemplate jdbc;
    public BusBookingService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Passenger(String name, int age, String gender, String idType, String idNumber) {}
    public record BusBooking(UUID id, String bookingRef, String pnr, UUID busId, String status,
                             List<String> seats, int passengerCount, double totalFare, String bookedAt) {}
    public record BusSeat(String seatNumber, String type, String deck, boolean isAvailable, double price) {}

    public List<BusBooking> getMyBusBookings(UUID userId) {
        return jdbc.query(
            "SELECT id, booking_ref, pnr, bus_id, status, seats, passenger_count, total_fare, booked_at " +
            "FROM bus_schema.bus_bookings WHERE user_id = :userId ORDER BY booked_at DESC",
            Map.of("userId", userId),
            (rs, row) -> new BusBooking(rs.getObject("id", UUID.class), rs.getString("booking_ref"),
                rs.getString("pnr"), rs.getObject("bus_id", UUID.class), rs.getString("status"),
                List.of(rs.getString("seats").split(",")), rs.getInt("passenger_count"),
                rs.getDouble("total_fare"), rs.getString("booked_at")));
    }

    public BusBooking getBusBooking(String bookingRef) {
        return jdbc.queryForObject(
            "SELECT id, booking_ref, pnr, bus_id, status, seats, passenger_count, total_fare, booked_at " +
            "FROM bus_schema.bus_bookings WHERE booking_ref = :ref",
            Map.of("ref", bookingRef),
            (rs, row) -> new BusBooking(rs.getObject("id", UUID.class), rs.getString("booking_ref"),
                rs.getString("pnr"), rs.getObject("bus_id", UUID.class), rs.getString("status"),
                List.of(rs.getString("seats").split(",")), rs.getInt("passenger_count"),
                rs.getDouble("total_fare"), rs.getString("booked_at")));
    }

    @Transactional
    public BusBooking bookBus(UUID userId, String busId, String date, List<String> seats,
                               String boardingPoint, String droppingPoint, List<Map<String, Object>> passengers) {
        String bookingRef = "BS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String pnr = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO bus_schema.bus_bookings (id, user_id, bus_id, booking_ref, pnr, status, seats, passenger_count, total_fare, booked_at) " +
            "VALUES (:id, :userId, :busId, :ref, :pnr, 'CONFIRMED', :seats, :paxCount, 2500.0, NOW())",
            Map.of("id", id, "userId", userId, "busId", UUID.fromString(busId), "ref", bookingRef,
                "pnr", pnr, "seats", String.join(",", seats), "paxCount", passengers != null ? passengers.size() : 0));
        return new BusBooking(id, bookingRef, pnr, UUID.fromString(busId), "CONFIRMED", seats, passengers != null ? passengers.size() : 0, 2500.0, java.time.LocalDateTime.now().toString());
    }

    @Transactional
    public BusBooking cancelBus(String bookingRef) {
        jdbc.update("UPDATE bus_schema.bus_bookings SET status = 'CANCELLED' WHERE booking_ref = :ref", Map.of("ref", bookingRef));
        return getBusBooking(bookingRef);
    }
}
