package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.InternalControlLog;
import com.example.finance.service.InternalControlLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内控预警日志控制器
 */
@RestController
@RequestMapping("/api/internal-control")
@CrossOrigin(origins = "*")
public class InternalControlController {

    @Autowired
    private InternalControlLogService internalControlService;

    /**
     * 查询所有预警日志
     */
    @GetMapping("/list")
    public Result<List<InternalControlLog>> listAll() {
        List<InternalControlLog> result = internalControlService.listAll();
        return Result.success(result);
    }

    /**
     * 按预警类型查询
     */
    @GetMapping("/list/{alertType}")
    public Result<List<InternalControlLog>> listByAlertType(@PathVariable String alertType) {
        List<InternalControlLog> result = internalControlService.listByAlertType(alertType);
        return Result.success(result);
    }

    /**
     * 按处理状态查询
     */
    @GetMapping("/list/status/{handleStatus}")
    public Result<List<InternalControlLog>> listByHandleStatus(@PathVariable String handleStatus) {
        List<InternalControlLog> result = internalControlService.listByHandleStatus(handleStatus);
        return Result.success(result);
    }

    /**
     * 更新预警处理状态
     */
    @PutMapping("/update-status")
    public Result<Boolean> updateHandleStatus(
            @RequestParam String logId,
            @RequestParam String handleStatus,
            @RequestParam String handler) {
        boolean result = internalControlService.updateHandleStatus(logId, handleStatus, handler);
        return result ? Result.success("更新成功", true) : Result.error("更新失败");
    }
}
