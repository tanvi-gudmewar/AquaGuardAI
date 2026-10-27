package com.aquaguard.service;

import com.aquaguard.dto.RepairRequest;
import com.aquaguard.entity.LeakEvent;
import com.aquaguard.entity.RepairRecord;
import com.aquaguard.repository.LeakEventRepository;
import com.aquaguard.repository.RepairRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RepairService {
    private final LeakEventRepository events;
    private final RepairRecordRepository repairs;
    public RepairService(LeakEventRepository events, RepairRecordRepository repairs) {
        this.events = events; this.repairs = repairs;
    }

    @Transactional
    public Map<String, Object> repair(Long id, RepairRequest request) {
        LeakEvent event = events.findById(id).orElseThrow(() -> new NoSuchElementException("Leak event not found: " + id));
        double excessFlow = Math.max(0, event.getFlowRateLpm() - event.getBaselineFlowLpm());
        double dailySaved = Math.max(0, excessFlow - request.postRepairFlowLpm()) * 60 * 8;
        event.setStatus("REPAIRED");
        event.setRepairedAt(LocalDateTime.now());
        event.setPostRepairFlowLpm(request.postRepairFlowLpm());
        events.save(event);
        RepairRecord record = repairs.save(new RepairRecord(id, excessFlow, request.postRepairFlowLpm(), dailySaved));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("repairRecordId", record.getId());
        result.put("leakEventId", id);
        result.put("estimatedExcessFlowLpm", excessFlow);
        result.put("postRepairFlowLpm", request.postRepairFlowLpm());
        result.put("estimatedDailyLitersSaved", dailySaved);
        result.put("assumedLeakDurationHoursPerDay", 8);
        result.put("status", "REPAIRED");
        return result;
    }
}
