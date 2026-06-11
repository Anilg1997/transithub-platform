package com.transithub.analytics.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    private final NamedParameterJdbcTemplate jdbc;

    public AnalyticsService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record DailyRevenue(String date, double flightRevenue, double busRevenue, double trainRevenue, double totalRevenue) {}
    public record BookingByMode(String mode, int count, double revenue, int cancellations) {}
    public record PopularRoute(String from, String to, String mode, double avgFare, int bookingsCount) {}

    public List<DailyRevenue> getDailyRevenue(String from, String to) {
        return jdbc.query("""
            SELECT d::date as date,
                   COALESCE((SELECT SUM(total_fare) FROM flight_schema.flight_bookings WHERE DATE(booked_at) = d AND status = 'CONFIRMED'), 0) as flight_revenue,
                   COALESCE((SELECT SUM(total_fare) FROM bus_schema.bus_bookings WHERE DATE(booked_at) = d AND status = 'CONFIRMED'), 0) as bus_revenue,
                   COALESCE((SELECT SUM(total_fare) FROM train_schema.train_bookings WHERE DATE(booked_at) = d AND status = 'CONFIRMED'), 0) as train_revenue
            FROM generate_series(:from::date, :to::date, '1 day'::interval) d
            ORDER BY d
            """, Map.of("from", from, "to", to),
            (rs, row) -> new DailyRevenue(
                rs.getString("date"), rs.getDouble("flight_revenue"),
                rs.getDouble("bus_revenue"), rs.getDouble("train_revenue"),
                rs.getDouble("flight_revenue") + rs.getDouble("bus_revenue") + rs.getDouble("train_revenue")));
    }

    public List<BookingByMode> getBookingsByMode(String from, String to) {
        return jdbc.query("""
            SELECT 'FLIGHT' as mode, COUNT(*) as count, COALESCE(SUM(total_fare), 0) as revenue,
                   COUNT(*) FILTER (WHERE status = 'CANCELLED') as cancellations
            FROM flight_schema.flight_bookings WHERE booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day'
            UNION ALL
            SELECT 'BUS', COUNT(*), COALESCE(SUM(total_fare), 0),
                   COUNT(*) FILTER (WHERE status = 'CANCELLED')
            FROM bus_schema.bus_bookings WHERE booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day'
            UNION ALL
            SELECT 'TRAIN', COUNT(*), COALESCE(SUM(total_fare), 0),
                   COUNT(*) FILTER (WHERE status = 'CANCELLED')
            FROM train_schema.train_bookings WHERE booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day'
            """, Map.of("from", from, "to", to),
            (rs, row) -> new BookingByMode(
                rs.getString("mode"), rs.getInt("count"),
                rs.getDouble("revenue"), rs.getInt("cancellations")));
    }

    public double getCancellationRate(String from, String to) {
        var result = jdbc.queryForMap("""
            SELECT
              (SELECT COUNT(*) FROM flight_schema.flight_bookings WHERE status = 'CANCELLED' AND booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day') +
              (SELECT COUNT(*) FROM bus_schema.bus_bookings WHERE status = 'CANCELLED' AND booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day') +
              (SELECT COUNT(*) FROM train_schema.train_bookings WHERE status = 'CANCELLED' AND booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day') as cancelled,
              (SELECT COUNT(*) FROM flight_schema.flight_bookings WHERE booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day') +
              (SELECT COUNT(*) FROM bus_schema.bus_bookings WHERE booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day') +
              (SELECT COUNT(*) FROM train_schema.train_bookings WHERE booked_at BETWEEN :from::timestamp AND :to::timestamp + interval '1 day') as total
            """, Map.of("from", from, "to", to));
        long total = ((Number) result.get("total")).longValue();
        long cancelled = ((Number) result.get("cancelled")).longValue();
        return total == 0 ? 0.0 : (double) cancelled / total * 100;
    }

    public int getTotalUsers() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM auth_schema.users", Map.of(), Integer.class);
    }

    public List<PopularRoute> getTopRoutes(String mode, int limit) {
        return jdbc.query("""
            (SELECT origin, destination, 'FLIGHT' as mode, AVG(total_fare) as avg_fare, COUNT(*) as cnt
             FROM flight_schema.flight_bookings WHERE status = 'CONFIRMED'
             GROUP BY origin, destination ORDER BY cnt DESC LIMIT :limit)
            UNION ALL
            (SELECT origin, destination, 'BUS', AVG(total_fare), COUNT(*)
             FROM bus_schema.bus_bookings WHERE status = 'CONFIRMED'
             GROUP BY origin, destination ORDER BY cnt DESC LIMIT :limit)
            UNION ALL
            (SELECT origin, destination, 'TRAIN', AVG(total_fare), COUNT(*)
             FROM train_schema.train_bookings WHERE status = 'CONFIRMED'
             GROUP BY origin, destination ORDER BY cnt DESC LIMIT :limit)
            ORDER BY cnt DESC LIMIT :limit
            """, Map.of("limit", limit),
            (rs, row) -> new PopularRoute(
                rs.getString("origin"), rs.getString("destination"),
                rs.getString("mode"), rs.getDouble("avg_fare"), rs.getInt("cnt")));
    }
}
