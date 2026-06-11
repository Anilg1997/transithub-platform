package com.transithub.flight.inventory.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class FlightInventoryService {
    private final NamedParameterJdbcTemplate jdbc;
    public FlightInventoryService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Airport(String code, String name, String city, String country, String terminal) {}
    public record Flight(String id, String flightNumber, String airline, String airlineCode,
                         Airport origin, Airport destination, String departureTime, String arrivalTime,
                         int duration, int stops, String cabinClass, int totalSeats, int availableSeats,
                         double baseFare, double taxes, double totalFare, boolean isRefundable, double cancellationFee) {}
    public record SeatMap(List<SeatRow> rows) {}
    public record SeatRow(int rowNumber, List<Seat> seats) {}
    public record Seat(String seatNumber, String type, boolean isAvailable, boolean extraLegroom, double price) {}

    public List<Flight> searchFlights(String origin, String destination, String date, String cabinClass, Integer passengers) {
        return jdbc.query("""
            SELECT id, flight_number, airline, airline_code, origin_code, destination_code,
                   departure_time, arrival_time, duration, stops, cabin_class, total_seats,
                   available_seats, base_fare, taxes, total_fare, is_refundable, cancellation_fee
            FROM flight_schema.flights
            WHERE origin_code = :origin AND destination_code = :destination
              AND DATE(departure_time) = :date
              AND (:cabinClass IS NULL OR cabin_class = :cabinClass)
            ORDER BY departure_time
            """, Map.of("origin", origin, "destination", destination, "date", date, "cabinClass", cabinClass),
            (rs, row) -> new Flight(rs.getString("id"), rs.getString("flight_number"), rs.getString("airline"),
                rs.getString("airline_code"),
                new Airport(rs.getString("origin_code"), "", "", "", ""),
                new Airport(rs.getString("destination_code"), "", "", "", ""),
                rs.getString("departure_time"), rs.getString("arrival_time"), rs.getInt("duration"), rs.getInt("stops"),
                rs.getString("cabin_class"), rs.getInt("total_seats"), rs.getInt("available_seats"),
                rs.getDouble("base_fare"), rs.getDouble("taxes"), rs.getDouble("total_fare"),
                rs.getBoolean("is_refundable"), rs.getDouble("cancellation_fee")));
    }

    public Flight flightDetail(String flightId) {
        return jdbc.queryForObject(
            "SELECT id, flight_number, airline, airline_code, origin_code, destination_code, departure_time, arrival_time, " +
            "duration, stops, cabin_class, total_seats, available_seats, base_fare, taxes, total_fare, is_refundable, cancellation_fee " +
            "FROM flight_schema.flights WHERE id = :id",
            Map.of("id", flightId),
            (rs, row) -> new Flight(rs.getString("id"), rs.getString("flight_number"), rs.getString("airline"),
                rs.getString("airline_code"),
                new Airport(rs.getString("origin_code"), "", "", "", ""),
                new Airport(rs.getString("destination_code"), "", "", "", ""),
                rs.getString("departure_time"), rs.getString("arrival_time"), rs.getInt("duration"), rs.getInt("stops"),
                rs.getString("cabin_class"), rs.getInt("total_seats"), rs.getInt("available_seats"),
                rs.getDouble("base_fare"), rs.getDouble("taxes"), rs.getDouble("total_fare"),
                rs.getBoolean("is_refundable"), rs.getDouble("cancellation_fee")));
    }

    public SeatMap seatMap(String flightId, String date) {
        var seats = jdbc.query(
            "SELECT seat_number, seat_type, is_available, extra_legroom, price FROM flight_schema.seats WHERE flight_id = :flightId ORDER BY seat_number",
            Map.of("flightId", flightId),
            (rs, row) -> new Seat(rs.getString("seat_number"), rs.getString("seat_type"),
                rs.getBoolean("is_available"), rs.getBoolean("extra_legroom"), rs.getDouble("price")));
        var rows = new java.util.ArrayList<SeatRow>();
        var grouped = seats.stream().collect(java.util.stream.Collectors.groupingBy(
            s -> Integer.parseInt(s.seatNumber().replaceAll("[^0-9]", ""))));
        grouped.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e ->
            rows.add(new SeatRow(e.getKey(), e.getValue())));
        return new SeatMap(rows);
    }

    public List<Airport> airports(String query) {
        return jdbc.query(
            "SELECT code, name, city, country, terminal FROM flight_schema.airports WHERE name ILIKE :q OR code ILIKE :q OR city ILIKE :q LIMIT 10",
            Map.of("q", "%" + query + "%"),
            (rs, row) -> new Airport(rs.getString("code"), rs.getString("name"),
                rs.getString("city"), rs.getString("country"), rs.getString("terminal")));
    }
}
