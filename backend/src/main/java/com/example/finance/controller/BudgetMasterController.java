package com.example.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.dto.Result;
import com.example.finance.entity.BudgetMaster;
import com.example.finance.service.BudgetMasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预算管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/budget/master")
@CrossOrigin(origins = "*")
public class BudgetMasterController {
    
    @Autowired
    private BudgetMasterService budgetMasterService;
    
    @GetMapping("/list")
    public Result<List<BudgetMaster>> list() {
        try {
            List<BudgetMaster> list = budgetMasterService.listAll();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询预算失败", e);
            return Result.error("查询预算失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/page")
    public Result<Page<BudgetMaster>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<BudgetMaster> result = budgetMasterService.pageBudget(pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询预算失败", e);
            return Result.error("分页查询预算失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/year/{year}")
    public Result<List<BudgetMaster>> listByYear(@PathVariable Integer year) {
        try {
            List<BudgetMaster> list = budgetMasterService.listByYear(year);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按年度查询预算失败", e);
            return Result.error("按年度查询预算失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/dept/{deptCode}")
    public Result<List<BudgetMaster>> listByDept(@PathVariable String deptCode) {
        try {
            List<BudgetMaster> list = budgetMasterService.listByDept(deptCode);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按部门查询预算失败", e);
            return Result.error("按部门查询预算失败：" + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<Boolean> save(@RequestBody BudgetMaster budget) {
        try {
            // 设置默认状态为草稿
            budget.setStatus("DRAFT");
            boolean result = budgetMasterService.save(budget);
            return Result.success(result);
        } catch (Exception e) {
            log.error("新增预算失败", e);
            return Result.error("新增预算失败：" + e.getMessage());
        }
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody BudgetMaster budget) {
        try {
            boolean result = budgetMasterService.updateById(budget);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新预算失败", e);
            return Result.error("更新预算失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            boolean result = budgetMasterService.removeById(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除预算失败", e);
            return Result.error("删除预算失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/approve")
    public Result<Boolean> approve(@PathVariable Long id,
                                   @RequestParam Long approverId,
                                   @RequestParam String approverName) {
        try {
            boolean result = budgetMasterService.approveBudget(id, approverId, approverName);
            if (!result) {
                return Result.error("审批失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("审批预算失败", e);
            return Result.error("审批预算失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reject")
    public Result<Boolean> reject(@PathVariable Long id,
                                  @RequestParam Long approverId,
                                  @RequestParam String approverName,
                                  @RequestParam(required = false) String reason) {
        try {
            boolean result = budgetMasterService.rejectBudget(id, approverId, approverName, reason);
            if (!result) {
                return Result.error("驳回失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("驳回预算失败", e);
            return Result.error("驳回预算失败：" + e.getMessage());
        }
    }
}
