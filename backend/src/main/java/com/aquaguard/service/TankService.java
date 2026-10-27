package com.aquaguard.service;

import com.aquaguard.dto.TankRequest;
import com.aquaguard.entity.WaterTank;
import com.aquaguard.repository.WaterTankRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TankService {
    private final WaterTankRepository repository;
    public TankService(WaterTankRepository repository) { this.repository = repository; }

    public List<WaterTank> all() { return repository.findAll(); }

    public WaterTank create(TankRequest request) {
        if (request.currentVolumeL() > request.capacityL()) {
            throw new IllegalArgumentException("Current volume cannot exceed tank capacity.");
        }
        WaterTank tank = new WaterTank();
        tank.setName(request.name());
        tank.setCapacityL(request.capacityL());
        tank.setCurrentVolumeL(request.currentVolumeL());
        tank.setIncomingFlowLpm(request.incomingFlowLpm());
        tank.setOutgoingFlowLpm(request.outgoingFlowLpm());
        tank.setWarningThresholdPercent(request.warningThresholdPercent());
        return repository.save(tank);
    }

    public Map<String, Object> predict(Long id) {
        WaterTank tank = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Tank not found: " + id));
        double fillPercent = tank.getCurrentVolumeL() / tank.getCapacityL() * 100.0;
        double remaining = Math.max(0, tank.getCapacityL() - tank.getCurrentVolumeL());
        double netRate = tank.getIncomingFlowLpm() - tank.getOutgoingFlowLpm();
        Double minutesToFull = netRate > 0 && remaining > 0 ? remaining / netRate : null;
        String risk = fillPercent >= 95 ? "CRITICAL" :
                fillPercent >= tank.getWarningThresholdPercent() ? "WARNING" :
                minutesToFull != null && minutesToFull <= 30 ? "WARNING" : "NORMAL";
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tankId", id);
        result.put("fillPercent", Math.round(fillPercent * 10.0) / 10.0);
        result.put("remainingCapacityL", remaining);
        result.put("netFlowLpm", netRate);
        result.put("minutesToFull", minutesToFull);
        result.put("risk", risk);
        result.put("assumption", "Constant current incoming and outgoing flow; estimate only.");
        return result;
    }
}
