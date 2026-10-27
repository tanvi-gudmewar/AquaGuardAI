package com.aquaguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leak_events")
public class LeakEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String zone;
    private double flowRateLpm;
    private double baselineFlowLpm;
    private double riskScore;
    @Column(nullable = false, length = 30)
    private String status = "SUSPECTED";
    @Column(nullable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();
    private LocalDateTime repairedAt;
    private double postRepairFlowLpm;

    public LeakEvent() {}
    public LeakEvent(String zone, double flowRateLpm, double baselineFlowLpm, double riskScore) {
        this.zone = zone; this.flowRateLpm = flowRateLpm; this.baselineFlowLpm = baselineFlowLpm;
        this.riskScore = riskScore; this.detectedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getZone() { return zone; }
    public double getFlowRateLpm() { return flowRateLpm; }
    public double getBaselineFlowLpm() { return baselineFlowLpm; }
    public double getRiskScore() { return riskScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public LocalDateTime getRepairedAt() { return repairedAt; }
    public void setRepairedAt(LocalDateTime repairedAt) { this.repairedAt = repairedAt; }
    public double getPostRepairFlowLpm() { return postRepairFlowLpm; }
    public void setPostRepairFlowLpm(double postRepairFlowLpm) { this.postRepairFlowLpm = postRepairFlowLpm; }
}
