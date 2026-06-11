package com.transithub.audit.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "audit_logs")
public record AuditLog(
    @Id String id,
    String eventId,
    String eventType,
    String userId,
    String action,
    String entityType,
    String entityId,
    String payload,
    String ipAddress,
    LocalDateTime timestamp
) {
    public AuditLog {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (timestamp == null) timestamp = LocalDateTime.now();
    }
}
