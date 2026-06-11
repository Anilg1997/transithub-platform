package com.transithub.auth.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "auth_schema", name = "refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private UUID userId;
    @Column(nullable = false) private String tokenHash;
    @Column(nullable = false) private LocalDateTime expiresAt;
    private boolean revoked = false;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt = LocalDateTime.now();
    public RefreshToken() {}
    public RefreshToken(UUID userId, String tokenHash, LocalDateTime expiresAt) {
        this.userId = userId; this.tokenHash = tokenHash; this.expiresAt = expiresAt;
    }
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean r) { this.revoked = r; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}
