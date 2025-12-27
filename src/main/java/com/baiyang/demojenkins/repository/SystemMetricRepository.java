package com.baiyang.demojenkins.repository;

import com.baiyang.demojenkins.entity.SystemMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemMetricRepository extends JpaRepository<SystemMetric, Long> {
}
