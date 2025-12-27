package com.baiyang.demojenkins.service;

import com.baiyang.demojenkins.entity.SystemMetric;
import com.baiyang.demojenkins.repository.SystemMetricRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.HashMap;
import java.util.Map;

@Service
public class MonitorService {

    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final SystemMetricRepository repository;
    private long[] prevTicks;

    public MonitorService(SystemMetricRepository repository) {
        this.repository = repository;
        this.systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.prevTicks = hardware.getProcessor().getSystemCpuLoadTicks();
    }

    public Map<String, Object> getRealtimeMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Memory Info
        GlobalMemory memory = hardware.getMemory();
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        double memUsage = (double) usedMemory / totalMemory * 100;

        metrics.put("memoryTotal", totalMemory);
        metrics.put("memoryUsed", usedMemory);
        metrics.put("memoryAvailable", availableMemory);
        metrics.put("memoryUsageUnformatted", memUsage);

        // CPU Info
        CentralProcessor processor = hardware.getProcessor();
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = processor.getSystemCpuLoadTicks();

        metrics.put("cpuLoad", String.format("%.1f", cpuLoad));
        metrics.put("cpuCores", processor.getLogicalProcessorCount());

        // Save to DB (Fire and forget, in real app maybe use @Scheduled)
        saveMetric(cpuLoad, memUsage, usedMemory);

        return metrics;
    }

    private void saveMetric(double cpu, double mem, long memUsed) {
        // Simple async-like save (in transactional context of request usually)
        if (!Double.isNaN(cpu)) {
            repository.save(new SystemMetric(cpu, mem, memUsed));
        }
    }
}
