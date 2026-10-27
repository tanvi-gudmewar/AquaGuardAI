package com.aquaguard.repository;

import com.aquaguard.entity.WaterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WaterReadingRepository extends JpaRepository<WaterReading, Long> {
    List<WaterReading> findTop100ByOrderByTimestampDesc();
}
