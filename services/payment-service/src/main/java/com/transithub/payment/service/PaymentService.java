package com.transithub.payment.service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {
    private final NamedParameterJdbcTemplate jdbc;
    public PaymentService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public record Payment(UUID id, String transactionId, String bookingRef, double amount, double gst,
                          double convenienceFee, double totalAmount, String status, String method,
                          String initiatedAt, String completedAt) {}

    public Payment getPaymentStatus(String transactionId) {
        return jdbc.queryForObject(
            "SELECT id, transaction_id, booking_ref, amount, gst, convenience_fee, total_amount, status, method, initiated_at, completed_at " +
            "FROM payment_schema.payments WHERE transaction_id = :txId",
            Map.of("txId", transactionId),
            (rs, row) -> new Payment(rs.getObject("id", UUID.class), rs.getString("transaction_id"),
                rs.getString("booking_ref"), rs.getDouble("amount"), rs.getDouble("gst"),
                rs.getDouble("convenience_fee"), rs.getDouble("total_amount"),
                rs.getString("status"), rs.getString("method"),
                rs.getString("initiated_at"), rs.getString("completed_at")));
    }

    public List<Payment> getMyPayments(UUID userId) {
        return jdbc.query(
            "SELECT id, transaction_id, booking_ref, amount, gst, convenience_fee, total_amount, status, method, initiated_at, completed_at " +
            "FROM payment_schema.payments WHERE user_id = :userId ORDER BY initiated_at DESC",
            Map.of("userId", userId),
            (rs, row) -> new Payment(rs.getObject("id", UUID.class), rs.getString("transaction_id"),
                rs.getString("booking_ref"), rs.getDouble("amount"), rs.getDouble("gst"),
                rs.getDouble("convenience_fee"), rs.getDouble("total_amount"),
                rs.getString("status"), rs.getString("method"),
                rs.getString("initiated_at"), rs.getString("completed_at")));
    }

    @Transactional
    public Payment initiatePayment(UUID userId, String bookingRef, double amount, String method, String gstNumber) {
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        UUID id = UUID.randomUUID();
        double gst = amount * 0.18;
        double convenienceFee = amount * 0.02;
        double totalAmount = amount + gst + convenienceFee;
        jdbc.update(
            "INSERT INTO payment_schema.payments (id, user_id, transaction_id, booking_ref, amount, gst, convenience_fee, total_amount, status, method, initiated_at) " +
            "VALUES (:id, :userId, :txId, :bookingRef, :amount, :gst, :fee, :total, 'INITIATED', :method, NOW())",
            Map.of("id", id, "userId", userId, "txId", transactionId, "bookingRef", bookingRef,
                "amount", amount, "gst", gst, "fee", convenienceFee, "total", totalAmount, "method", method));
        return new Payment(id, transactionId, bookingRef, amount, gst, convenienceFee, totalAmount, "INITIATED", method,
            java.time.LocalDateTime.now().toString(), null);
    }

    @Transactional
    public Payment confirmPayment(String transactionId, Boolean mockSuccess) {
        String newStatus = Boolean.TRUE.equals(mockSuccess) ? "SUCCESS" : "FAILED";
        jdbc.update("UPDATE payment_schema.payments SET status = :status, completed_at = NOW() WHERE transaction_id = :txId",
            Map.of("status", newStatus, "txId", transactionId));
        return getPaymentStatus(transactionId);
    }

    @Transactional
    public Payment walletPay(UUID userId, String bookingRef) {
        String transactionId = "TXN-W-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        UUID id = UUID.randomUUID();
        jdbc.update(
            "INSERT INTO payment_schema.payments (id, user_id, transaction_id, booking_ref, amount, gst, convenience_fee, total_amount, status, method, initiated_at, completed_at) " +
            "VALUES (:id, :userId, :txId, :bookingRef, 0, 0, 0, 0, 'SUCCESS', 'WALLET', NOW(), NOW())",
            Map.of("id", id, "userId", userId, "txId", transactionId, "bookingRef", bookingRef));
        return getPaymentStatus(transactionId);
    }
}
