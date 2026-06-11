package com.transithub.refund.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "refund_schema", name = "refunds")
public class Refund {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(unique = true, nullable = false) private String refundRef;
    @Column(nullable = false) private String bookingRef;
    private double amount;
    private String refundType;
    private String status = "INITIATED";
    private String reason;
    private LocalDateTime initiatedAt = LocalDateTime.now();
    private LocalDateTime completedAt;
    public UUID getId() { return id; }
    public String getRefundRef() { return refundRef; }
    public void setRefundRef(String r) { this.refundRef = r; }
    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String r) { this.bookingRef = r; }
    public double getAmount() { return amount; }
    public void setAmount(double a) { this.amount = a; }
    public String getRefundType() { return refundType; }
    public void setRefundType(String t) { this.refundType = t; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime t) { this.initiatedAt = t; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime t) { this.completedAt = t; }
}
