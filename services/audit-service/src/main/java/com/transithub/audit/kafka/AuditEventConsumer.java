package com.transithub.audit.kafka;
import com.transithub.audit.model.AuditLog;
import com.transithub.audit.repository.AuditLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class AuditEventConsumer {
    private final AuditLogRepository repo;

    public AuditEventConsumer(AuditLogRepository repo) { this.repo = repo; }

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = "th.audit.events", groupId = "audit-service-group")
    public void consume(@Payload String payload) {
        var log = new AuditLog(null, java.util.UUID.randomUUID().toString(), "SYSTEM_EVENT",
            null, "KAFKA_EVENT", "EVENT", null, payload, null, null);
        repo.save(log);
    }
}
