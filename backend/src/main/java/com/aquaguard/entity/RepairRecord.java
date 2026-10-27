package com.aquaguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_records")
public class RepairRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long leakEventId;
    private double estimatedExcessFlowLpm;
    private double postRepairFlowLpm;
    private double estimatedDailyLitersSaved;
    @Column(nullable = false)
    private LocalDateTime repairedAt = LocalDateTime.now();

    public RepairRecord() {}
    public RepairRecord(Long leakEventId, double estimatedExcessFlowLpm, double postRepairFlowLpm, double estimatedDailyLitersSaved) {
        this.leakEventId = leakEventId; this.estimatedExcessFlowLpm = estimatedExcessFlowLpm;
        this.postRepairFlowLpm = postRepairFlowLpm; this.estimatedDailyLitersSaved = estimatedDailyLitersSaved;
        this.repairedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Long getLeakEventId() { return leakEventId; }
    public double getEstimatedExcessFlowLpm() { return estimatedExcessFlowLpm; }
    public double getPostRepairFlowLpm() { return postRepairFlowLpm; }
    public double getEstimatedDailyLitersSaved() { return estimatedDailyLitersSaved; }
    public LocalDateTime getRepairedAt() { return repairedAt; }
}
