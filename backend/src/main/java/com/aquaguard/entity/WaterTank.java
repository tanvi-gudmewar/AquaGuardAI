package com.aquaguard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "water_tanks")
public class WaterTank {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false)
    private double capacityL;
    @Column(nullable = false)
    private double currentVolumeL;
    @Column(nullable = false)
    private double incomingFlowLpm;
    @Column(nullable = false)
    private double outgoingFlowLpm;
    @Column(nullable = false)
    private double warningThresholdPercent = 90;

    public WaterTank() {}
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCapacityL() { return capacityL; }
    public void setCapacityL(double capacityL) { this.capacityL = capacityL; }
    public double getCurrentVolumeL() { return currentVolumeL; }
    public void setCurrentVolumeL(double currentVolumeL) { this.currentVolumeL = currentVolumeL; }
    public double getIncomingFlowLpm() { return incomingFlowLpm; }
    public void setIncomingFlowLpm(double incomingFlowLpm) { this.incomingFlowLpm = incomingFlowLpm; }
    public double getOutgoingFlowLpm() { return outgoingFlowLpm; }
    public void setOutgoingFlowLpm(double outgoingFlowLpm) { this.outgoingFlowLpm = outgoingFlowLpm; }
    public double getWarningThresholdPercent() { return warningThresholdPercent; }
    public void setWarningThresholdPercent(double warningThresholdPercent) { this.warningThresholdPercent = warningThresholdPercent; }
}
