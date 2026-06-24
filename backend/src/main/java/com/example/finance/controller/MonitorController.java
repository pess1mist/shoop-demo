package com.example.finance.controller;

import com.example.finance.dto.Result;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalRevenue", "1000000.00");
        dashboard.put("totalCost", "650000.00");
        dashboard.put("profit", "350000.00");
        dashboard.put("budgetExecutionRate", "65.00");
        dashboard.put("alertCount", 5);
        return Result.success(dashboard);
    }

    @GetMapping("/department/{deptCode}")
    public Result<Map<String, Object>> getDepartmentMonitor(@PathVariable String deptCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("departmentCode", deptCode);
        result.put("totalRevenue", "500000.00");
        result.put("totalCost", "300000.00");
        result.put("profit", "200000.00");
        return Result.success(result);
    }
}
