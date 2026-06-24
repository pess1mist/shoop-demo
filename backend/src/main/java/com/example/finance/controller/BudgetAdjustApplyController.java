package com.example.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.dto.Result;
import com.example.finance.entity.BudgetAdjustApply;
import com.example.finance.service.BudgetAdjustApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预算调整申请 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/budget/adjust")
@CrossOrigin(origins = "*")
public class BudgetAdjustApplyController {
    
    @Autowired
    private BudgetAdjustApplyService budgetAdjustApplyService;
    
    @GetMapping("/list")
    public Result<List<BudgetAdjustApply>> list() {
        try {
            List<BudgetAdjustApply> list = budgetAdjustApplyService.listAll();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询调整申请失败", e);
            return Result.error("查询调整申请失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/page")
    public Result<Page<BudgetAdjustApply>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<BudgetAdjustApply> result = budgetAdjustApplyService.pageAdjustApply(pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询调整申请失败", e);
            return Result.error("分页查询调整申请失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/budget/{budgetId}")
    public Result<List<BudgetAdjustApply>> listByBudgetId(@PathVariable Long budgetId) {
        try {
            List<BudgetAdjustApply> list = budgetAdjustApplyService.listByBudgetId(budgetId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按预算 ID 查询调整申请失败", e);
            return Result.error("按预算 ID 查询调整申请失败：" + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<Boolean> save(@RequestBody BudgetAdjustApply apply) {
        try {
            // 设置默认状态为待审批
            apply.setStatus("PENDING");
            boolean result = budgetAdjustApplyService.save(apply);
            return Result.success(result);
        } catch (Exception e) {
            log.error("新增调整申请失败", e);
            return Result.error("新增调整申请失败：" + e.getMessage());
        }
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody BudgetAdjustApply apply) {
        try {
            boolean result = budgetAdjustApplyService.updateById(apply);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新调整申请失败", e);
            return Result.error("更新调整申请失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            boolean result = budgetAdjustApplyService.removeById(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除调整申请失败", e);
            return Result.error("删除调整申请失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/approve")
    public Result<Boolean> approve(@PathVariable Long id,
                                   @RequestParam Long approverId,
                                   @RequestParam String approverName,
                                   @RequestParam(required = false) String remark) {
        try {
            boolean result = budgetAdjustApplyService.approveApply(id, approverId, approverName, remark);
            if (!result) {
                return Result.error("审批失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("审批调整申请失败", e);
            return Result.error("审批调整申请失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reject")
    public Result<Boolean> reject(@PathVariable Long id,
                                  @RequestParam Long approverId,
                                  @RequestParam String approverName,
                                  @RequestParam(required = false) String remark) {
        try {
            boolean result = budgetAdjustApplyService.rejectApply(id, approverId, approverName, remark);
            if (!result) {
                return Result.error("驳回失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("驳回调整申请失败", e);
            return Result.error("驳回调整申请失败：" + e.getMessage());
        }
    }
}
