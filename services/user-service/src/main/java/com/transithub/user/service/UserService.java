package com.transithub.user.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    private final NamedParameterJdbcTemplate jdbc;
    public UserService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record UserProfile(UUID id, UUID userId, String fullName, String dateOfBirth, String gender, int loyaltyPoints, String loyaltyTier) {}
    public record Traveller(UUID id, String name, int age, String gender, String idType, String idNumber, boolean isPrimary) {}
    public record Wallet(UUID id, double balance, UUID userId) {}
    public record WalletTransaction(UUID id, String type, double amount, String description, String createdAt) {}
    public record BookingHistory(String mode, String bookingRef, String status, double totalFare, String bookedAt) {}

    public UserProfile getMyProfile(UUID userId) {
        return jdbc.queryForObject(
            "SELECT id, user_id, full_name, date_of_birth, gender, loyalty_points, loyalty_tier FROM user_schema.user_profiles WHERE user_id = :userId",
            Map.of("userId", userId),
            (rs, row) -> new UserProfile(rs.getObject("id", UUID.class), rs.getObject("user_id", UUID.class),
                rs.getString("full_name"), rs.getString("date_of_birth"), rs.getString("gender"),
                rs.getInt("loyalty_points"), rs.getString("loyalty_tier")));
    }

    @Transactional
    public UserProfile updateProfile(UUID userId, String fullName, String dateOfBirth, String gender) {
        jdbc.update("UPDATE user_schema.user_profiles SET full_name = COALESCE(:fullName, full_name), " +
            "date_of_birth = COALESCE(:dob, date_of_birth), gender = COALESCE(:gender, gender) WHERE user_id = :userId",
            Map.of("userId", userId, "fullName", fullName, "dob", dateOfBirth, "gender", gender));
        return getMyProfile(userId);
    }

    public List<Traveller> getMyTravellers(UUID userId) {
        return jdbc.query("SELECT id, name, age, gender, id_type, id_number, is_primary FROM user_schema.travellers WHERE user_id = :userId",
            Map.of("userId", userId),
            (rs, row) -> new Traveller(rs.getObject("id", UUID.class), rs.getString("name"), rs.getInt("age"),
                rs.getString("gender"), rs.getString("id_type"), rs.getString("id_number"), rs.getBoolean("is_primary")));
    }

    @Transactional
    public Traveller addTraveller(UUID userId, String name, int age, String gender, String idType, String idNumber) {
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO user_schema.travellers (id, user_id, name, age, gender, id_type, id_number, is_primary) " +
            "VALUES (:id, :userId, :name, :age, :gender, :idType, :idNumber, false)",
            Map.of("id", id, "userId", userId, "name", name, "age", age, "gender", gender, "idType", idType, "idNumber", idNumber));
        return new Traveller(id, name, age, gender, idType, idNumber, false);
    }

    @Transactional
    public Traveller updateTraveller(UUID travellerId, String name, Integer age, String gender, String idType, String idNumber) {
        jdbc.update("UPDATE user_schema.travellers SET name = COALESCE(:name, name), age = COALESCE(:age, age), " +
            "gender = COALESCE(:gender, gender), id_type = COALESCE(:idType, id_type), id_number = COALESCE(:idNumber, id_number) WHERE id = :id",
            Map.of("id", travellerId, "name", name, "age", age, "gender", gender, "idType", idType, "idNumber", idNumber));
        return jdbc.queryForObject("SELECT id, name, age, gender, id_type, id_number, is_primary FROM user_schema.travellers WHERE id = :id",
            Map.of("id", travellerId),
            (rs, row) -> new Traveller(rs.getObject("id", UUID.class), rs.getString("name"), rs.getInt("age"),
                rs.getString("gender"), rs.getString("id_type"), rs.getString("id_number"), rs.getBoolean("is_primary")));
    }

    @Transactional
    public boolean deleteTraveller(UUID travellerId) {
        return jdbc.update("DELETE FROM user_schema.travellers WHERE id = :id", Map.of("id", travellerId)) > 0;
    }

    public int getLoyaltyBalance(UUID userId) {
        return jdbc.queryForObject("SELECT COALESCE(loyalty_points, 0) FROM user_schema.user_profiles WHERE user_id = :userId",
            Map.of("userId", userId), Integer.class);
    }

    public double getWalletBalance(UUID userId) {
        var result = jdbc.queryForMap("SELECT COALESCE(balance, 0) as balance FROM user_schema.wallets WHERE user_id = :userId",
            Map.of("userId", userId));
        return ((Number) result.get("balance")).doubleValue();
    }

    @Transactional
    public Wallet addMoneyToWallet(UUID userId, double amount) {
        jdbc.update("UPDATE user_schema.wallets SET balance = balance + :amount WHERE user_id = :userId",
            Map.of("userId", userId, "amount", amount));
        var result = jdbc.queryForMap("SELECT id, balance, user_id FROM user_schema.wallets WHERE user_id = :userId",
            Map.of("userId", userId));
        return new Wallet(result.get("id") instanceof UUID u ? u : UUID.fromString(result.get("id").toString()),
            ((Number) result.get("balance")).doubleValue(),
            result.get("user_id") instanceof UUID u ? u : UUID.fromString(result.get("user_id").toString()));
    }

    public List<BookingHistory> getMyBookings(UUID userId) {
        var list = new java.util.ArrayList<BookingHistory>();
        jdbc.query("SELECT 'FLIGHT' as mode, booking_ref, status, total_fare, booked_at FROM flight_schema.flight_bookings WHERE user_id = :userId " +
            "UNION ALL SELECT 'BUS', booking_ref, status, total_fare, booked_at FROM bus_schema.bus_bookings WHERE user_id = :userId " +
            "UNION ALL SELECT 'TRAIN', booking_ref, status, total_fare, booked_at FROM train_schema.train_bookings WHERE user_id = :userId ORDER BY booked_at DESC",
            Map.of("userId", userId),
            (rs, row) -> list.add(new BookingHistory(rs.getString("mode"), rs.getString("booking_ref"),
                rs.getString("status"), rs.getDouble("total_fare"), rs.getString("booked_at"))));
        return list;
    }
}
