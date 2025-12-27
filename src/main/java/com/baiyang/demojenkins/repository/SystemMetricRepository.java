package com.baiyang.demojenkins.repository;

import com.baiyang.demojenkins.entity.SystemMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemMetricRepository extends JpaRepository<SystemMetric, Long> {
    List<SystemMetric> findTop100ByOrderByTimestampDesc();
}
