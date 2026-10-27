package com.aquaguard.controller;

import com.aquaguard.dto.*;
import com.aquaguard.entity.*;
import com.aquaguard.repository.LeakEventRepository;
import com.aquaguard.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final WaterReadingService readings;
    private final LeakDetectionService leakDetection;
    private final RepairService repairService;
    private final TankService tanks;
    private final AnalyticsService analytics;
    private final SimulationService simulation;
    private final LeakEventRepository leakRepository;

    public ApiController(WaterReadingService readings, LeakDetectionService leakDetection,
                         RepairService repairService, TankService tanks, AnalyticsService analytics,
                         SimulationService simulation, LeakEventRepository leakRepository) {
        this.readings = readings; this.leakDetection = leakDetection; this.repairService = repairService;
        this.tanks = tanks; this.analytics = analytics; this.simulation = simulation; this.leakRepository = leakRepository;
    }

    @GetMapping("/health")
    public Map<String, String> health() { return Map.of("status", "UP", "application", "AquaGuard AI"); }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() { return analytics.dashboard(); }

    @GetMapping("/readings")
    public List<WaterReading> getReadings() { return readings.latest(); }

    @PostMapping("/readings")
    public ResponseEntity<WaterReading> createReading(@Valid @RequestBody ReadingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(readings.create(request));
    }

    @GetMapping("/leaks")
    public List<LeakEvent> getLeaks() { return leakDetection.all(); }

    @PostMapping("/leaks/detect")
    public List<LeakEvent> detectLeaks() { return leakDetection.detect(); }

    @PatchMapping("/leaks/{id}/status")
    public LeakEvent updateLeakStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return leakDetection.updateStatus(id, body.getOrDefault("status", ""));
    }

    @PostMapping("/leaks/{id}/repair")
    public Map<String, Object> repair(@PathVariable Long id, @Valid @RequestBody RepairRequest request) {
        return repairService.repair(id, request);
    }

    @GetMapping("/tanks")
    public List<WaterTank> getTanks() { return tanks.all(); }

    @PostMapping("/tanks")
    public ResponseEntity<WaterTank> createTank(@Valid @RequestBody TankRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tanks.create(request));
    }

    @GetMapping("/tanks/{id}/prediction")
    public Map<String, Object> tankPrediction(@PathVariable Long id) { return tanks.predict(id); }

    @GetMapping("/analytics/consumption")
    public Map<String, Object> consumption() { return analytics.consumption(); }

    @GetMapping("/analytics/savings")
    public Map<String, Object> savings() { return analytics.savings(); }

    @GetMapping(value = "/analytics/savings/export", produces = "text/csv")
    public ResponseEntity<String> exportSavings() {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=water-savings.csv")
                .contentType(MediaType.parseMediaType("text/csv")).body(analytics.savingsCsv());
    }

    @PostMapping("/simulation/start")
    public Map<String, Object> startSimulation() { return simulation.start(); }

    @PostMapping("/simulation/stop")
    public Map<String, Object> stopSimulation() { return simulation.stop(); }

    @GetMapping("/simulation/status")
    public Map<String, Object> simulationStatus() { return simulation.status(); }
}
