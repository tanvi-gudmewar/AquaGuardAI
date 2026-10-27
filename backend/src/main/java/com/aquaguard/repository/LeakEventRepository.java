package com.aquaguard.repository;

import com.aquaguard.entity.LeakEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeakEventRepository extends JpaRepository<LeakEvent, Long> {
    List<LeakEvent> findAllByOrderByDetectedAtDesc();
    long countByStatusNot(String status);
}
