package com.baiyang.demojenkins.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class SystemMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private Double cpuLoad;
    private Double memoryUsage; // percentage
    private Long memoryUsed; // bytes

    public SystemMetric() {}

    public SystemMetric(Double cpuLoad, Double memoryUsage, Long memoryUsed) {
        this.timestamp = LocalDateTime.now();
        this.cpuLoad = cpuLoad;
        this.memoryUsage = memoryUsage;
        this.memoryUsed = memoryUsed;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Double getCpuLoad() {
        return cpuLoad;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public Long getMemoryUsed() {
        return memoryUsed;
    }
}
