package com.example.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.dto.Result;
import com.example.finance.entity.AlertWorkOrder;
import com.example.finance.service.AlertWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预警工单 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/alert/work-order")
@CrossOrigin(origins = "*")
public class AlertWorkOrderController {
    
    @Autowired
    private AlertWorkOrderService alertWorkOrderService;
    
    @GetMapping("/list")
    public Result<List<AlertWorkOrder>> list() {
        try {
            List<AlertWorkOrder> list = alertWorkOrderService.listAll();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询工单失败", e);
            return Result.error("查询工单失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/page")
    public Result<Page<AlertWorkOrder>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<AlertWorkOrder> result = alertWorkOrderService.pageWorkOrder(pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询工单失败", e);
            return Result.error("分页查询工单失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/alert/{alertId}")
    public Result<List<AlertWorkOrder>> listByAlertId(@PathVariable Long alertId) {
        try {
            List<AlertWorkOrder> list = alertWorkOrderService.listByAlertId(alertId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按预警 ID 查询工单失败", e);
            return Result.error("按预警 ID 查询工单失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/status/{status}")
    public Result<List<AlertWorkOrder>> listByStatus(@PathVariable String status) {
        try {
            List<AlertWorkOrder> list = alertWorkOrderService.listByStatus(status);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按状态查询工单失败", e);
            return Result.error("按状态查询工单失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/assignee/{assigneeId}")
    public Result<List<AlertWorkOrder>> listByAssignee(@PathVariable Long assigneeId) {
        try {
            List<AlertWorkOrder> list = alertWorkOrderService.listByAssignee(assigneeId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按处理人查询工单失败", e);
            return Result.error("按处理人查询工单失败：" + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<Boolean> save(@RequestBody AlertWorkOrder workOrder) {
        try {
            boolean result = alertWorkOrderService.createWorkOrder(workOrder);
            return Result.success(result);
        } catch (Exception e) {
            log.error("新增工单失败", e);
            return Result.error("新增工单失败：" + e.getMessage());
        }
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody AlertWorkOrder workOrder) {
        try {
            boolean result = alertWorkOrderService.updateById(workOrder);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新工单失败", e);
            return Result.error("更新工单失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            boolean result = alertWorkOrderService.removeById(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除工单失败", e);
            return Result.error("删除工单失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/assign")
    public Result<Boolean> assign(@PathVariable Long id,
                                  @RequestParam Long assigneeId,
                                  @RequestParam String assigneeName) {
        try {
            boolean result = alertWorkOrderService.assignWorkOrder(id, assigneeId, assigneeName);
            if (!result) {
                return Result.error("分配失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("分配工单失败", e);
            return Result.error("分配工单失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/resolve")
    public Result<Boolean> resolve(@PathVariable Long id,
                                   @RequestParam String solution) {
        try {
            boolean result = alertWorkOrderService.resolveWorkOrder(id, solution);
            if (!result) {
                return Result.error("解决失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("解决工单失败", e);
            return Result.error("解决工单失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/close")
    public Result<Boolean> close(@PathVariable Long id,
                                 @RequestParam Long closedBy) {
        try {
            boolean result = alertWorkOrderService.closeWorkOrder(id, closedBy);
            if (!result) {
                return Result.error("关闭失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("关闭工单失败", e);
            return Result.error("关闭工单失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reject")
    public Result<Boolean> reject(@PathVariable Long id,
                                  @RequestParam(required = false) String reason) {
        try {
            boolean result = alertWorkOrderService.rejectWorkOrder(id, reason);
            if (!result) {
                return Result.error("驳回失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("驳回工单失败", e);
            return Result.error("驳回工单失败：" + e.getMessage());
        }
    }
}
