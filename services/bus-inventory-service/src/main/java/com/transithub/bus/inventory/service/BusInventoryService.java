package com.transithub.bus.inventory.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class BusInventoryService {
    private final NamedParameterJdbcTemplate jdbc;
    public BusInventoryService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record BusRoute(String id, String operator, String busType, double operatorRating,
                           String origin, String destination, String departureTime, String arrivalTime,
                           int duration, int totalSeats, int availableSeats, double fare,
                           List<String> boardingPoints, List<String> droppingPoints, String cancellationPolicy) {}
    public record BusSeat(String seatNumber, String type, String deck, boolean isAvailable, double price) {}

    public List<BusRoute> searchBuses(String origin, String destination, String date) {
        return jdbc.query(
            "SELECT id, operator, bus_type, operator_rating, origin, destination, departure_time, arrival_time, " +
            "duration, total_seats, available_seats, fare, boarding_points, dropping_points, cancellation_policy " +
            "FROM bus_schema.bus_routes WHERE origin = :origin AND destination = :destination AND is_active = true ORDER BY departure_time",
            Map.of("origin", origin, "destination", destination),
            (rs, row) -> new BusRoute(rs.getString("id"), rs.getString("operator"), rs.getString("bus_type"),
                rs.getDouble("operator_rating"), rs.getString("origin"), rs.getString("destination"),
                rs.getString("departure_time"), rs.getString("arrival_time"), rs.getInt("duration"),
                rs.getInt("total_seats"), rs.getInt("available_seats"), rs.getDouble("fare"),
                List.of(rs.getString("boarding_points").split(",")),
                List.of(rs.getString("dropping_points").split(",")),
                rs.getString("cancellation_policy")));
    }

    public BusRoute busDetail(String busId) {
        return jdbc.queryForObject(
            "SELECT id, operator, bus_type, operator_rating, origin, destination, departure_time, arrival_time, " +
            "duration, total_seats, available_seats, fare, boarding_points, dropping_points, cancellation_policy " +
            "FROM bus_schema.bus_routes WHERE id = :id",
            Map.of("id", busId),
            (rs, row) -> new BusRoute(rs.getString("id"), rs.getString("operator"), rs.getString("bus_type"),
                rs.getDouble("operator_rating"), rs.getString("origin"), rs.getString("destination"),
                rs.getString("departure_time"), rs.getString("arrival_time"), rs.getInt("duration"),
                rs.getInt("total_seats"), rs.getInt("available_seats"), rs.getDouble("fare"),
                List.of(rs.getString("boarding_points").split(",")),
                List.of(rs.getString("dropping_points").split(",")),
                rs.getString("cancellation_policy")));
    }

    public List<BusSeat> busSeatMap(String busId, String date) {
        return jdbc.query(
            "SELECT seat_number, seat_type, deck, is_available, price FROM bus_schema.bus_seats WHERE bus_id = :busId ORDER BY seat_number",
            Map.of("busId", busId),
            (rs, row) -> new BusSeat(rs.getString("seat_number"), rs.getString("seat_type"),
                rs.getString("deck"), rs.getBoolean("is_available"), rs.getDouble("price")));
    }
}
