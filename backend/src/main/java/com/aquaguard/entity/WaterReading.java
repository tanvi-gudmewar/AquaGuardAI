package com.aquaguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_readings", indexes = @Index(name = "idx_reading_timestamp", columnList = "timestamp"))
public class WaterReading {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String zone;
    @Column(nullable = false)
    private double flowRateLpm;
    private double tankLevelPercent;
    @Column(nullable = false, length = 20)
    private String source = "SIMULATED";
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public WaterReading() {}
    public WaterReading(String zone, double flowRateLpm, double tankLevelPercent, String source) {
        this.zone = zone; this.flowRateLpm = flowRateLpm;
        this.tankLevelPercent = tankLevelPercent; this.source = source;
        this.timestamp = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    public double getFlowRateLpm() { return flowRateLpm; }
    public void setFlowRateLpm(double flowRateLpm) { this.flowRateLpm = flowRateLpm; }
    public double getTankLevelPercent() { return tankLevelPercent; }
    public void setTankLevelPercent(double tankLevelPercent) { this.tankLevelPercent = tankLevelPercent; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
