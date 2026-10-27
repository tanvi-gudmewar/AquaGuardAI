package com.aquaguard.service;

import com.aquaguard.dto.ReadingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SimulationService {
    private final WaterReadingService readings;
    private final Random random = new Random(47);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private long ticks = 0;

    public SimulationService(WaterReadingService readings) { this.readings = readings; }

    public Map<String, Object> start() { running.set(true); return status(); }
    public Map<String, Object> stop() { running.set(false); return status(); }
    public Map<String, Object> status() {
        return Map.of("running", running.get(), "mode", "SIMULATED", "intervalMs", 5000);
    }

    @Scheduled(fixedDelayString = "${app.simulation.interval-ms:5000}")
    public void tick() {
        if (!running.get()) return;
        ticks++;
        double flow = 1.0 + random.nextDouble() * 2.0;
        // Every tenth tick creates an obvious high-flow demonstration event.
        if (ticks % 10 == 0) flow = 9.0 + random.nextDouble() * 3.0;
        readings.create(new ReadingRequest("Household", Math.round(flow * 10.0) / 10.0,
                40 + random.nextDouble() * 55, "SIMULATED"));
    }
}
