package com.transithub.auth.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "auth_schema", name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(unique = true) private String email;
    @Column(unique = true) private String phone;
    @Column(nullable = false) private String passwordHash;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false) private boolean isActive = true;
    private boolean emailVerified = false;
    private boolean phoneVerified = false;
    @Column(nullable = false) private String role = "ROLE_USER";
    private LocalDateTime lastLoginAt;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false) private LocalDateTime updatedAt = LocalDateTime.now();
    public User() {}
    public User(String email, String phone, String passwordHash, String fullName) {
        this.email = email; this.phone = phone; this.passwordHash = passwordHash; this.fullName = fullName;
    }
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public boolean isActive() { return isActive; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public void setLastLoginAt(LocalDateTime t) { this.lastLoginAt = t; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
