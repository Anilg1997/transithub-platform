package com.transithub.admin.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminService {
    private final NamedParameterJdbcTemplate jdbc;
    public AdminService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record FraudAlert(UUID id, UUID userId, String alertType, String description, String severity,
                             boolean isResolved, String createdAt) {}
    public record AdminUser(UUID id, String email, String phone, String fullName, boolean isActive,
                            String role, String createdAt) {}
    public record Flight(String id, String flightNumber, String airlineCode, String origin, String destination,
                         String departureTime, String arrivalTime, int duration, String cabinClass,
                         int totalSeats, double baseFare) {}
    public record BusRoute(String id, String operator, String busType, String origin, String destination,
                           String departureTime, String arrivalTime, int duration, int totalSeats,
                           double fare, List<String> boardingPoints, List<String> droppingPoints) {}
    public record Train(String id, String trainNumber, String trainName, String originCode, String destinationCode,
                        String departureTime, String arrivalTime, int duration, List<String> runsOnDays) {}

    public List<FraudAlert> getFraudAlerts() {
        return jdbc.query(
            "SELECT id, user_id, alert_type, description, severity, is_resolved, created_at FROM admin_schema.fraud_alerts ORDER BY created_at DESC",
            Map.of(),
            (rs, row) -> new FraudAlert(rs.getObject("id", UUID.class), rs.getObject("user_id", UUID.class),
                rs.getString("alert_type"), rs.getString("description"), rs.getString("severity"),
                rs.getBoolean("is_resolved"), rs.getString("created_at")));
    }

    public List<AdminUser> getAllUsers(Integer page, Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        return jdbc.query(
            "SELECT id, email, phone, full_name, is_active, role, created_at FROM auth_schema.users ORDER BY created_at DESC LIMIT :limit OFFSET :offset",
            Map.of("limit", s, "offset", (long) p * s),
            (rs, row) -> new AdminUser(rs.getObject("id", UUID.class), rs.getString("email"),
                rs.getString("phone"), rs.getString("full_name"), rs.getBoolean("is_active"),
                rs.getString("role"), rs.getString("created_at")));
    }

    @Transactional
    public Flight addFlight(String flightNumber, String airlineCode, String origin, String destination,
                            String departureTime, String arrivalTime, int duration, String cabinClass,
                            int totalSeats, double baseFare) {
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO flight_schema.flights (id, flight_number, airline_code, origin_code, destination_code, " +
            "departure_time, arrival_time, duration, cabin_class, total_seats, base_fare, taxes, total_fare, is_refundable, cancellation_fee) " +
            "VALUES (:id, :fn, :ac, :orig, :dest, :dep, :arr, :dur, :cc, :ts, :bf, 0, :bf, true, 0)",
            Map.ofEntries(
                Map.entry("id", id),
                Map.entry("fn", flightNumber),
                Map.entry("ac", airlineCode),
                Map.entry("orig", origin),
                Map.entry("dest", destination),
                Map.entry("dep", departureTime),
                Map.entry("arr", arrivalTime),
                Map.entry("dur", duration),
                Map.entry("cc", cabinClass),
                Map.entry("ts", totalSeats),
                Map.entry("bf", baseFare)));
        return new Flight(id.toString(), flightNumber, airlineCode, origin, destination,
            departureTime, arrivalTime, duration, cabinClass, totalSeats, baseFare);
    }

    @Transactional
    public Flight updateFlight(String flightId, String flightNumber, String airlineCode, String origin, String destination,
                               String departureTime, String arrivalTime, int duration, String cabinClass,
                               int totalSeats, double baseFare) {
        jdbc.update(
            "UPDATE flight_schema.flights SET flight_number = :fn, airline_code = :ac, origin_code = :orig, " +
            "destination_code = :dest, departure_time = :dep, arrival_time = :arr, duration = :dur, " +
            "cabin_class = :cc, total_seats = :ts, base_fare = :bf WHERE id = :id",
            Map.ofEntries(
                Map.entry("id", UUID.fromString(flightId)),
                Map.entry("fn", flightNumber),
                Map.entry("ac", airlineCode),
                Map.entry("orig", origin),
                Map.entry("dest", destination),
                Map.entry("dep", departureTime),
                Map.entry("arr", arrivalTime),
                Map.entry("dur", duration),
                Map.entry("cc", cabinClass),
                Map.entry("ts", totalSeats),
                Map.entry("bf", baseFare)));
        return new Flight(flightId, flightNumber, airlineCode, origin, destination,
            departureTime, arrivalTime, duration, cabinClass, totalSeats, baseFare);
    }

    @Transactional
    public boolean deleteFlight(String flightId) {
        return jdbc.update("DELETE FROM flight_schema.flights WHERE id = :id", Map.of("id", UUID.fromString(flightId))) > 0;
    }

    @Transactional
    public BusRoute addBusRoute(String operatorId, String busType, String origin, String destination,
                                String departureTime, String arrivalTime, int duration, int totalSeats,
                                double fare, List<String> boardingPoints, List<String> droppingPoints) {
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO bus_schema.bus_routes (id, operator, bus_type, origin, destination, departure_time, " +
            "arrival_time, duration, total_seats, available_seats, fare, boarding_points, dropping_points, cancellation_policy, is_active) " +
            "VALUES (:id, :op, :bt, :orig, :dest, :dep, :arr, :dur, :ts, :ts, :fare, :bp, :dp, 'Standard policy', true)",
            Map.ofEntries(
                Map.entry("id", id),
                Map.entry("op", operatorId),
                Map.entry("bt", busType),
                Map.entry("orig", origin),
                Map.entry("dest", destination),
                Map.entry("dep", departureTime),
                Map.entry("arr", arrivalTime),
                Map.entry("dur", duration),
                Map.entry("ts", totalSeats),
                Map.entry("fare", fare),
                Map.entry("bp", String.join(",", boardingPoints != null ? boardingPoints : List.of())),
                Map.entry("dp", String.join(",", droppingPoints != null ? droppingPoints : List.of()))));
        return new BusRoute(id.toString(), operatorId, busType, origin, destination,
            departureTime, arrivalTime, duration, totalSeats, fare, boardingPoints, droppingPoints);
    }

    @Transactional
    public BusRoute updateBusRoute(String routeId, String operatorId, String busType, String origin, String destination,
                                   String departureTime, String arrivalTime, int duration, int totalSeats,
                                   double fare, List<String> boardingPoints, List<String> droppingPoints) {
        jdbc.update(
            "UPDATE bus_schema.bus_routes SET operator = :op, bus_type = :bt, origin = :orig, destination = :dest, " +
            "departure_time = :dep, arrival_time = :arr, duration = :dur, total_seats = :ts, fare = :fare, " +
            "boarding_points = :bp, dropping_points = :dp WHERE id = :id",
            Map.ofEntries(
                Map.entry("id", UUID.fromString(routeId)),
                Map.entry("op", operatorId),
                Map.entry("bt", busType),
                Map.entry("orig", origin),
                Map.entry("dest", destination),
                Map.entry("dep", departureTime),
                Map.entry("arr", arrivalTime),
                Map.entry("dur", duration),
                Map.entry("ts", totalSeats),
                Map.entry("fare", fare),
                Map.entry("bp", String.join(",", boardingPoints != null ? boardingPoints : List.of())),
                Map.entry("dp", String.join(",", droppingPoints != null ? droppingPoints : List.of()))));
        return new BusRoute(routeId, operatorId, busType, origin, destination,
            departureTime, arrivalTime, duration, totalSeats, fare, boardingPoints, droppingPoints);
    }

    @Transactional
    public boolean deleteBusRoute(String routeId) {
        return jdbc.update("DELETE FROM bus_schema.bus_routes WHERE id = :id", Map.of("id", UUID.fromString(routeId))) > 0;
    }

    @Transactional
    public Train addTrain(String trainNumber, String trainName, String originCode, String destinationCode,
                          String departureTime, String arrivalTime, int duration, List<String> runsOnDays) {
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO train_schema.trains (id, train_number, train_name, origin_code, destination_code, " +
            "departure_time, arrival_time, duration, runs_on) VALUES (:id, :tn, :tname, :oc, :dc, :dep, :arr, :dur, :ro)",
            Map.of("id", id, "tn", trainNumber, "tname", trainName, "oc", originCode, "dc", destinationCode,
                "dep", departureTime, "arr", arrivalTime, "dur", duration,
                "ro", String.join(",", runsOnDays)));
        return new Train(id.toString(), trainNumber, trainName, originCode, destinationCode,
            departureTime, arrivalTime, duration, runsOnDays);
    }

    @Transactional
    public Train updateTrain(String trainId, String trainNumber, String trainName, String originCode, String destinationCode,
                             String departureTime, String arrivalTime, int duration, List<String> runsOnDays) {
        jdbc.update(
            "UPDATE train_schema.trains SET train_number = :tn, train_name = :tname, origin_code = :oc, " +
            "destination_code = :dc, departure_time = :dep, arrival_time = :arr, duration = :dur, runs_on = :ro WHERE id = :id",
            Map.of("id", UUID.fromString(trainId), "tn", trainNumber, "tname", trainName, "oc", originCode,
                "dc", destinationCode, "dep", departureTime, "arr", arrivalTime, "dur", duration,
                "ro", String.join(",", runsOnDays)));
        return new Train(trainId, trainNumber, trainName, originCode, destinationCode,
            departureTime, arrivalTime, duration, runsOnDays);
    }

    @Transactional
    public boolean deleteTrain(String trainId) {
        return jdbc.update("DELETE FROM train_schema.trains WHERE id = :id", Map.of("id", UUID.fromString(trainId))) > 0;
    }

    @Transactional
    public boolean banUser(String userId) {
        jdbc.update("UPDATE auth_schema.users SET is_active = false WHERE id = :id", Map.of("id", UUID.fromString(userId)));
        return true;
    }

    @Transactional
    public boolean unbanUser(String userId) {
        jdbc.update("UPDATE auth_schema.users SET is_active = true WHERE id = :id", Map.of("id", UUID.fromString(userId)));
        return true;
    }

    @Transactional
    public boolean overrideBooking(String bookingRef, String action) {
        String table = bookingRef.startsWith("FL") ? "flight_schema.flight_bookings" :
                       bookingRef.startsWith("BS") ? "bus_schema.bus_bookings" : "train_schema.train_bookings";
        jdbc.update("UPDATE " + table + " SET status = :action WHERE booking_ref = :ref",
            Map.of("action", action, "ref", bookingRef));
        return true;
    }
}
