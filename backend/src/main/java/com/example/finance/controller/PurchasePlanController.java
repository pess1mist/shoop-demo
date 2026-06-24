package com.example.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.dto.Result;
import com.example.finance.entity.PurchasePlan;
import com.example.finance.service.PurchasePlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购计划 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/purchase/plan")
@CrossOrigin(origins = "*")
public class PurchasePlanController {
    
    @Autowired
    private PurchasePlanService purchasePlanService;
    
    @GetMapping("/list")
    public Result<List<PurchasePlan>> list() {
        try {
            List<PurchasePlan> list = purchasePlanService.listAll();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询采购计划失败", e);
            return Result.error("查询采购计划失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/page")
    public Result<Page<PurchasePlan>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<PurchasePlan> result = purchasePlanService.pagePurchasePlan(pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询采购计划失败", e);
            return Result.error("分页查询采购计划失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/dept/{deptCode}")
    public Result<List<PurchasePlan>> listByDept(@PathVariable String deptCode) {
        try {
            List<PurchasePlan> list = purchasePlanService.listByDept(deptCode);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按部门查询采购计划失败", e);
            return Result.error("按部门查询采购计划失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/budget/{budgetId}")
    public Result<List<PurchasePlan>> listByBudgetId(@PathVariable Long budgetId) {
        try {
            List<PurchasePlan> list = purchasePlanService.listByBudgetId(budgetId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按预算 ID 查询采购计划失败", e);
            return Result.error("按预算 ID 查询采购计划失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/status/{status}")
    public Result<List<PurchasePlan>> listByStatus(@PathVariable String status) {
        try {
            List<PurchasePlan> list = purchasePlanService.listByStatus(status);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按状态查询采购计划失败", e);
            return Result.error("按状态查询采购计划失败：" + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<PurchasePlan> save(@RequestBody PurchasePlan purchasePlan) {
        try {
            // 调用 service 创建采购计划
            boolean result = purchasePlanService.createPurchasePlan(purchasePlan);
            if (result) {
                // 返回新创建的对象（包含自动生成的 ID）
                return Result.success(purchasePlan);
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            log.error("新增采购计划失败", e);
            return Result.error("新增采购计划失败：" + e.getMessage());
        }
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody PurchasePlan purchasePlan) {
        try {
            boolean result = purchasePlanService.updateById(purchasePlan);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新采购计划失败", e);
            return Result.error("更新采购计划失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            boolean result = purchasePlanService.removeById(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除采购计划失败", e);
            return Result.error("删除采购计划失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/submit")
    public Result<Boolean> submit(@PathVariable Long id,
                                  @RequestParam Long userId,
                                  @RequestParam String userName) {
        try {
            boolean result = purchasePlanService.submitForApproval(id, userId, userName);
            if (!result) {
                return Result.error("提交审批失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("提交审批失败", e);
            return Result.error("提交审批失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/approve")
    public Result<Boolean> approve(@PathVariable Long id,
                                   @RequestParam Long approverId,
                                   @RequestParam String approverName,
                                   @RequestParam(required = false) String opinion) {
        try {
            boolean result = purchasePlanService.approvePlan(id, approverId, approverName, opinion);
            if (!result) {
                return Result.error("审批通过失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("审批通过失败", e);
            return Result.error("审批通过失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reject")
    public Result<Boolean> reject(@PathVariable Long id,
                                  @RequestParam Long approverId,
                                  @RequestParam String approverName,
                                  @RequestParam String opinion) {
        try {
            boolean result = purchasePlanService.rejectPlan(id, approverId, approverName, opinion);
            if (!result) {
                return Result.error("驳回失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("驳回失败", e);
            return Result.error("驳回失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/complete")
    public Result<Boolean> complete(@PathVariable Long id) {
        try {
            boolean result = purchasePlanService.completePlan(id);
            if (!result) {
                return Result.error("完成采购失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("完成采购失败", e);
            return Result.error("完成采购失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询采购计划的审批进度
     * @param id 采购计划 ID
     * @return 审批进度信息
     */
    @GetMapping("/{id}/workflow-progress")
    public Result<Object> getWorkflowProgress(@PathVariable Long id) {
        try {
            // 获取采购计划
            PurchasePlan plan = purchasePlanService.getById(id);
            if (plan == null) {
                return Result.error("采购计划不存在");
            }
            
            // 如果没有关联工作流实例
            if (plan.getWorkflowInstanceId() == null) {
                return Result.success(null);
            }
            
            // 查询审批进度信息（由 Service 层实现）
            Object progress = purchasePlanService.getWorkflowProgress(id);
            return Result.success(progress);
        } catch (Exception e) {
            log.error("查询审批进度失败", e);
            return Result.error("查询审批进度失败：" + e.getMessage());
        }
    }
}
