package com.baiyang.demojenkins.controller;

import com.baiyang.demojenkins.service.MonitorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final MonitorService monitorService;
    private final long startTime = System.currentTimeMillis();

    public SystemController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping("/info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("appName", "Demo Jenkins App");
        info.put("version", "0.0.1-SNAPSHOT");
        info.put("serverTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        info.put("os", System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        info.put("javaVersion", System.getProperty("java.version"));
        
        long uptime = System.currentTimeMillis() - startTime;
        info.put("uptime", formatUptime(uptime));
        
        return info;
    }

    @GetMapping("/realtime")
    public Map<String, Object> getRealtimeMetrics() {
        return monitorService.getRealtimeMetrics();
    }

    @GetMapping("/history")
    public java.util.List<com.baiyang.demojenkins.entity.SystemMetric> getHistory() {
        return monitorService.getHistory();
    }

    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }
}
