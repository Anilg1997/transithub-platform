package com.transithub.auth.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "auth_schema", name = "outbox_events")
public class OutboxEvent {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private String aggregateType;
    @Column(nullable = false) private UUID aggregateId;
    @Column(nullable = false) private String eventType;
    @Column(columnDefinition = "jsonb", nullable = false) private String payload;
    private boolean published = false;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt = LocalDateTime.now();
    public OutboxEvent() {}
    public OutboxEvent(String aggregateType, UUID aggregateId, String eventType, String payload) {
        this.aggregateType = aggregateType; this.aggregateId = aggregateId; this.eventType = eventType; this.payload = payload;
    }
    public UUID getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public UUID getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean p) { this.published = p; }
}
