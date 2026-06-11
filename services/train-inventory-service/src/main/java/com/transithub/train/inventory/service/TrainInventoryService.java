package com.transithub.train.inventory.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class TrainInventoryService {
    private final NamedParameterJdbcTemplate jdbc;
    public TrainInventoryService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Station(String code, String name, String city, String state) {}
    public record Coach(String coachType, int totalBerths, int availableBerths, double fare) {}
    public record Train(String id, String trainNumber, String trainName, Station origin, Station destination,
                        String departureTime, String arrivalTime, int duration, List<String> runsOn, List<Coach> coaches) {}
    public record Berth(String berthNumber, String type, boolean isAvailable, double price) {}

    public List<Train> searchTrains(String from, String to, String date, String quota) {
        return jdbc.query(
            "SELECT t.id, t.train_number, t.train_name, t.departure_time, t.arrival_time, t.duration, t.runs_on, " +
            "o.code as o_code, o.name as o_name, o.city as o_city, o.state as o_state, " +
            "d.code as d_code, d.name as d_name, d.city as d_city, d.state as d_state " +
            "FROM train_schema.trains t " +
            "JOIN train_schema.stations o ON t.origin_code = o.code " +
            "JOIN train_schema.stations d ON t.destination_code = d.code " +
            "WHERE t.origin_code = :from AND t.destination_code = :to ORDER BY t.departure_time",
            Map.of("from", from, "to", to),
            (rs, row) -> new Train(rs.getString("id"), rs.getString("train_number"), rs.getString("train_name"),
                new Station(rs.getString("o_code"), rs.getString("o_name"), rs.getString("o_city"), rs.getString("o_state")),
                new Station(rs.getString("d_code"), rs.getString("d_name"), rs.getString("d_city"), rs.getString("d_state")),
                rs.getString("departure_time"), rs.getString("arrival_time"), rs.getInt("duration"),
                List.of(rs.getString("runs_on").split(",")), List.of()));
    }

    public Train trainDetail(String trainId) {
        return jdbc.queryForObject(
            "SELECT t.id, t.train_number, t.train_name, t.departure_time, t.arrival_time, t.duration, t.runs_on, " +
            "o.code as o_code, o.name as o_name, o.city as o_city, o.state as o_state, " +
            "d.code as d_code, d.name as d_name, d.city as d_city, d.state as d_state " +
            "FROM train_schema.trains t " +
            "JOIN train_schema.stations o ON t.origin_code = o.code " +
            "JOIN train_schema.stations d ON t.destination_code = d.code WHERE t.id = :id",
            Map.of("id", trainId),
            (rs, row) -> new Train(rs.getString("id"), rs.getString("train_number"), rs.getString("train_name"),
                new Station(rs.getString("o_code"), rs.getString("o_name"), rs.getString("o_city"), rs.getString("o_state")),
                new Station(rs.getString("d_code"), rs.getString("d_name"), rs.getString("d_city"), rs.getString("d_state")),
                rs.getString("departure_time"), rs.getString("arrival_time"), rs.getInt("duration"),
                List.of(rs.getString("runs_on").split(",")), List.of()));
    }

    public List<Berth> coachMap(String trainId, String date, String coachType) {
        return jdbc.query(
            "SELECT berth_number, berth_type, is_available, price FROM train_schema.berths WHERE train_id = :trainId AND coach_type = :coachType ORDER BY berth_number",
            Map.of("trainId", trainId, "coachType", coachType),
            (rs, row) -> new Berth(rs.getString("berth_number"), rs.getString("berth_type"),
                rs.getBoolean("is_available"), rs.getDouble("price")));
    }

    public List<Station> stations(String query) {
        return jdbc.query(
            "SELECT code, name, city, state FROM train_schema.stations WHERE name ILIKE :q OR code ILIKE :q OR city ILIKE :q LIMIT 10",
            Map.of("q", "%" + query + "%"),
            (rs, row) -> new Station(rs.getString("code"), rs.getString("name"),
                rs.getString("city"), rs.getString("state")));
    }
}
