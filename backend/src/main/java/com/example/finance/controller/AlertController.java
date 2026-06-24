package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.InternalControlLog;
import com.example.finance.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内控预警控制器
 */
@Tag(name = "内控预警", description = "预警日志查询与处理接口")
@RestController
@RequestMapping("/api/alert")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertService alertService;

    /**
     * 获取预警列表
     * GET /api/alert/list?status=未处理
     */
    @GetMapping("/list")
    @Operation(summary = "获取预警列表")
    public Result<List<InternalControlLog>> getAlertList(
            @RequestParam(required = false) String status) {
        
        List<InternalControlLog> data = alertService.getAlertList(status);
        return Result.success(data);
    }

    /**
     * 更新预警处理状态
     * PUT /api/alert/update-status
     */
    @PutMapping("/update-status")
    @Operation(summary = "更新预警状态")
    public Result<Boolean> updateAlertStatus(
            @RequestParam String logId,
            @RequestParam String status,
            @RequestParam String handler) {
        
        boolean result = alertService.updateAlertStatus(logId, status, handler);
        return result ? Result.success("更新成功", true) : Result.error("更新失败");
    }
}
