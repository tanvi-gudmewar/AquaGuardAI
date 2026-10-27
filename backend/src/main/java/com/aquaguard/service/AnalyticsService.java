package com.aquaguard.service;

import com.aquaguard.entity.LeakEvent;
import com.aquaguard.entity.RepairRecord;
import com.aquaguard.entity.WaterReading;
import com.aquaguard.entity.WaterTank;
import com.aquaguard.repository.LeakEventRepository;
import com.aquaguard.repository.RepairRecordRepository;
import com.aquaguard.repository.WaterReadingRepository;
import com.aquaguard.repository.WaterTankRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AnalyticsService {
    private final WaterReadingRepository readings;
    private final WaterTankRepository tanks;
    private final LeakEventRepository leaks;
    private final RepairRecordRepository repairs;

    public AnalyticsService(WaterReadingRepository readings, WaterTankRepository tanks,
                            LeakEventRepository leaks, RepairRecordRepository repairs) {
        this.readings = readings; this.tanks = tanks; this.leaks = leaks; this.repairs = repairs;
    }

    public Map<String, Object> dashboard() {
        List<WaterReading> rs = readings.findTop100ByOrderByTimestampDesc();
        double current = rs.isEmpty() ? 0 : rs.get(0).getFlowRateLpm();
        long active = leaks.findAllByOrderByDetectedAtDesc().stream().filter(e -> !Set.of("REPAIRED", "CLOSED").contains(e.getStatus())).count();
        long atRisk = tanks.findAll().stream().filter(t -> {
            double pct = t.getCurrentVolumeL() / t.getCapacityL() * 100;
            return pct >= t.getWarningThresholdPercent() || (t.getIncomingFlowLpm() > t.getOutgoingFlowLpm() &&
                    (t.getCapacityL() - t.getCurrentVolumeL()) / (t.getIncomingFlowLpm() - t.getOutgoingFlowLpm()) <= 30);
        }).count();
        double saved = repairs.findAll().stream().mapToDouble(RepairRecord::getEstimatedDailyLitersSaved).sum();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("monitoredZones", rs.stream().map(WaterReading::getZone).distinct().count());
        out.put("currentFlowLpm", current);
        out.put("activeLeaks", active);
        out.put("tanksAtRisk", atRisk);
        out.put("estimatedLitersSaved", saved);
        return out;
    }

    public Map<String, Object> consumption() {
        List<WaterReading> rs = readings.findTop100ByOrderByTimestampDesc();
        double average = rs.stream().mapToDouble(WaterReading::getFlowRateLpm).average().orElse(0);
        double latest = rs.isEmpty() ? 0 : rs.get(0).getFlowRateLpm();
        double deviation = average > 0 ? (latest - average) / average * 100 : 0;
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("readingCount", rs.size());
        out.put("averageFlowLpm", average);
        out.put("latestFlowLpm", latest);
        out.put("deviationFromRecentAveragePercent", deviation);
        out.put("explanation", "Comparison uses the latest stored reading against the average of up to 100 recent readings.");
        return out;
    }

    public Map<String, Object> savings() {
        List<RepairRecord> records = repairs.findAll();
        double cumulative = records.stream().mapToDouble(RepairRecord::getEstimatedDailyLitersSaved).sum();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("repairedLeaks", records.size());
        out.put("cumulativeLitersSaved", cumulative);
        out.put("todayLitersSaved", cumulative);
        out.put("monthlyLitersSaved", cumulative * 30);
        out.put("method", "Estimated daily savings are summed from repair records; monthly figure multiplies the estimate by 30.");
        return out;
    }

    public String savingsCsv() {
        StringBuilder csv = new StringBuilder("repairRecordId,leakEventId,estimatedExcessFlowLpm,postRepairFlowLpm,estimatedDailyLitersSaved,repairedAt\n");
        for (RepairRecord r : repairs.findAll()) {
            csv.append(r.getId()).append(',').append(r.getLeakEventId()).append(',')
               .append(r.getEstimatedExcessFlowLpm()).append(',').append(r.getPostRepairFlowLpm()).append(',')
               .append(r.getEstimatedDailyLitersSaved()).append(',').append(r.getRepairedAt()).append('\n');
        }
        return csv.toString();
    }
}
