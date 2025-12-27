package com.baiyang.demojenkins.service;

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
    private long[] prevTicks;

    public MonitorService() {
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

        metrics.put("memoryTotal", totalMemory);
        metrics.put("memoryUsed", usedMemory);
        metrics.put("memoryAvailable", availableMemory);
        // Helper for frontend percentage
        metrics.put("memoryUsageUnformatted", (double) usedMemory / totalMemory * 100);

        // CPU Info
        CentralProcessor processor = hardware.getProcessor();
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        prevTicks = processor.getSystemCpuLoadTicks();

        metrics.put("cpuLoad", String.format("%.1f", cpuLoad));
        metrics.put("cpuCores", processor.getLogicalProcessorCount());

        return metrics;
    }
}
