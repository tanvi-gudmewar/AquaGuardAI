package com.aquaguard.service;

import com.aquaguard.entity.LeakEvent;
import com.aquaguard.entity.WaterReading;
import com.aquaguard.repository.LeakEventRepository;
import com.aquaguard.repository.WaterReadingRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LeakDetectionService {
    private final WaterReadingRepository readings;
    private final LeakEventRepository events;
    private static final double DEFAULT_BASELINE_LPM = 1.5;
    private static final double ABSOLUTE_HIGH_FLOW_LPM = 8.0;

    public LeakDetectionService(WaterReadingRepository readings, LeakEventRepository events) {
        this.readings = readings; this.events = events;
    }

    public List<LeakEvent> all() { return events.findAllByOrderByDetectedAtDesc(); }

    public List<LeakEvent> detect() {
        List<WaterReading> latest = readings.findTop100ByOrderByTimestampDesc();
        List<LeakEvent> created = new ArrayList<>();
        for (WaterReading reading : latest) {
            double baseline = DEFAULT_BASELINE_LPM;
            double deviation = Math.max(0, reading.getFlowRateLpm() - baseline);
            boolean abnormal = reading.getFlowRateLpm() >= ABSOLUTE_HIGH_FLOW_LPM ||
                    (reading.getFlowRateLpm() >= 4.0 && deviation >= 2.5);
            if (!abnormal) continue;
            boolean duplicate = events.findAllByOrderByDetectedAtDesc().stream()
                    .anyMatch(e -> e.getZone().equals(reading.getZone())
                            && !e.getStatus().equals("REPAIRED")
                            && e.getDetectedAt().isAfter(reading.getTimestamp().minusMinutes(10)));
            if (!duplicate) {
                double score = Math.min(100, 50 + deviation * 5);
                created.add(events.save(new LeakEvent(reading.getZone(), reading.getFlowRateLpm(), baseline, score)));
            }
        }
        return created;
    }

    public LeakEvent updateStatus(Long id, String status) {
        LeakEvent event = events.findById(id).orElseThrow(() -> new NoSuchElementException("Leak event not found: " + id));
        if (!Set.of("ACKNOWLEDGED", "INVESTIGATING", "SUSPECTED", "CLOSED").contains(status)) {
            throw new IllegalArgumentException("Unsupported status.");
        }
        event.setStatus(status);
        return events.save(event);
    }
}
