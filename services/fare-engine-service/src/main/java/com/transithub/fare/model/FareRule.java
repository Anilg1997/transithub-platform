package com.transithub.fare.model;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(schema = "fare_schema", name = "fare_rules")
public class FareRule {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false) private String inventoryType;
    @Column(nullable = false) private String fareType;
    @Column(columnDefinition = "jsonb", nullable = false) private String ruleConfig;
    private boolean isActive = true;
    public FareRule() {}
    public UUID getId() { return id; }
    public String getInventoryType() { return inventoryType; }
    public String getFareType() { return fareType; }
    public String getRuleConfig() { return ruleConfig; }
    public boolean isActive() { return isActive; }
}
