package com.example.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.dto.Result;
import com.example.finance.entity.PurchaseApproval;
import com.example.finance.service.PurchaseApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购审批 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/purchase/approval")
@CrossOrigin(origins = "*")
public class PurchaseApprovalController {
    
    @Autowired
    private PurchaseApprovalService purchaseApprovalService;
    
    @GetMapping("/list")
    public Result<List<PurchaseApproval>> list() {
        try {
            List<PurchaseApproval> list = purchaseApprovalService.listAll();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询审批记录失败", e);
            return Result.error("查询审批记录失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/page")
    public Result<Page<PurchaseApproval>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<PurchaseApproval> result = purchaseApprovalService.pageApproval(pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询审批记录失败", e);
            return Result.error("分页查询审批记录失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/plan/{planId}")
    public Result<List<PurchaseApproval>> listByPlanId(@PathVariable Long planId) {
        try {
            List<PurchaseApproval> list = purchaseApprovalService.listByPlanId(planId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按采购计划 ID 查询审批记录失败", e);
            return Result.error("按采购计划 ID 查询审批记录失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/approver/{approverId}")
    public Result<List<PurchaseApproval>> listByApprover(@PathVariable Long approverId) {
        try {
            List<PurchaseApproval> list = purchaseApprovalService.listByApprover(approverId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按审批人查询审批记录失败", e);
            return Result.error("按审批人查询审批记录失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/result/{result}")
    public Result<List<PurchaseApproval>> listByResult(@PathVariable String result) {
        try {
            List<PurchaseApproval> list = purchaseApprovalService.listByResult(result);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按审批结果查询失败", e);
            return Result.error("按审批结果查询失败：" + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<Boolean> save(@RequestBody PurchaseApproval approval) {
        try {
            boolean result = purchaseApprovalService.createApproval(approval);
            return Result.success(result);
        } catch (Exception e) {
            log.error("新增审批记录失败", e);
            return Result.error("新增审批记录失败：" + e.getMessage());
        }
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody PurchaseApproval approval) {
        try {
            boolean result = purchaseApprovalService.updateById(approval);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新审批记录失败", e);
            return Result.error("更新审批记录失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            boolean result = purchaseApprovalService.removeById(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除审批记录失败", e);
            return Result.error("删除审批记录失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/do-approval")
    public Result<Boolean> doApproval(@PathVariable Long id,
                                      @RequestParam String result,
                                      @RequestParam(required = false) String opinion) {
        try {
            boolean resultBool = purchaseApprovalService.doApproval(id, result, opinion);
            if (!resultBool) {
                return Result.error("审批失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            log.error("审批失败", e);
            return Result.error("审批失败：" + e.getMessage());
        }
    }
}
