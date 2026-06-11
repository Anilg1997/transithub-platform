package com.transithub.refund.service;
import com.transithub.refund.model.Refund;
import com.transithub.refund.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RefundService {
    private final RefundRepository repo;
    public RefundService(RefundRepository repo) { this.repo = repo; }

    public record RefundResult(UUID id, String refundRef, String bookingRef, double amount, String refundType, String status, LocalDateTime initiatedAt, LocalDateTime completedAt) {}

    public double estimateRefund(String bookingRef, String cancelType) {
        // Simplified refund percentage based on cancellation type
        return switch (cancelType) {
            case "FULL" -> 100.0;
            case "PARTIAL" -> 50.0;
            case "NON_REFUNDABLE" -> 0.0;
            default -> 0.0;
        };
    }

    @Transactional
    public RefundResult processRefund(String bookingRef) {
        var refund = new Refund();
        refund.setRefundRef("RFD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        refund.setBookingRef(bookingRef);
        refund.setAmount(500.0);
        refund.setRefundType("AUTO");
        refund.setStatus("COMPLETED");
        refund.setInitiatedAt(LocalDateTime.now());
        refund.setCompletedAt(LocalDateTime.now());
        repo.save(refund);
        return new RefundResult(refund.getId(), refund.getRefundRef(), refund.getBookingRef(),
            refund.getAmount(), refund.getRefundType(), refund.getStatus(),
            refund.getInitiatedAt(), refund.getCompletedAt());
    }

    public List<RefundResult> getMyRefunds() {
        return repo.findAll().stream()
            .map(r -> new RefundResult(r.getId(), r.getRefundRef(), r.getBookingRef(),
                r.getAmount(), r.getRefundType(), r.getStatus(), r.getInitiatedAt(), r.getCompletedAt()))
            .toList();
    }
}
