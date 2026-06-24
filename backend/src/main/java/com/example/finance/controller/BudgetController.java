package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.BudgetExecution;
import com.example.finance.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

/**
 * 预算管理控制器
 */
@Tag(name = "预算管理", description = "预算执行数据查询接口")
@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:5173}")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    /**
     * 获取预算执行数据
     * GET /api/budget/execution?year=2025&item=材料费
     */
    @GetMapping("/execution")
    @Operation(summary = "获取预算执行数据")
    public Result<List<BudgetExecution>> getBudgetExecution(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String item) {
        
        if (year == null) {
            year = Year.now().getValue();
        }
        
        List<BudgetExecution> data = budgetService.getBudgetExecutionData(year, item);
        return Result.success(data);
    }

    /**
     * 获取部门预算汇总
     * GET /api/budget/department-summary?year=2025
     */
    @GetMapping("/department-summary")
    @Operation(summary = "获取部门预算汇总")
    public Result<List<BudgetExecution>> getDepartmentBudget(@RequestParam(required = false) Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        
        List<BudgetExecution> data = budgetService.getDepartmentBudget(year);
        return Result.success(data);
    }
}
