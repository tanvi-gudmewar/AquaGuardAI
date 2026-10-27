package com.aquaguard.service;

import com.aquaguard.dto.ReadingRequest;
import com.aquaguard.entity.WaterReading;
import com.aquaguard.repository.WaterReadingRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WaterReadingService {
    private final WaterReadingRepository repository;
    public WaterReadingService(WaterReadingRepository repository) { this.repository = repository; }

    public WaterReading create(ReadingRequest request) {
        return repository.save(new WaterReading(request.zone(), request.flowRateLpm(),
                request.tankLevelPercent(), request.source()));
    }
    public List<WaterReading> latest() { return repository.findTop100ByOrderByTimestampDesc(); }
    public List<WaterReading> all() { return repository.findAll(); }
}
