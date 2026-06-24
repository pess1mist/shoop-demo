package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.BudgetExecution;
import com.example.finance.service.BudgetExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 预算执行控制器
 */
@Tag(name = "预算执行", description = "提供预算执行数据查询接口")
@RestController
@RequestMapping("/api/budget-execution")
@CrossOrigin(origins = "*")
public class BudgetExecutionController {

    @Autowired
    private BudgetExecutionService budgetExecutionService;

    @Autowired
    private javax.sql.DataSource dataSource;

    /**
     * 获取预算执行数据（支持部门分布）
     */
    @Operation(summary = "获取预算执行数据", description = "返回预算执行的汇总数据、明细数据和部门分布数据")
    @GetMapping("/data")
    public Result<Map<String, Object>> getBudgetExecutionData(
            @RequestParam(required = false, defaultValue = "all") String department,
            @RequestParam(required = false) String monthRange) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 解析月份范围
            String startMonth = null;
            String endMonth = null;
            if (monthRange != null && monthRange.contains(",")) {
                String[] months = monthRange.split(",");
                startMonth = months[0];
                endMonth = months[1];
            }
            
            // 查询预算执行数据（从 budget_execution 表）
            List<BudgetExecution> executions = new ArrayList<>();
            
            // 使用 MyBatis-Plus 查询（更安全可靠）
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BudgetExecution> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            
            if (startMonth != null && endMonth != null) {
                wrapper.between("period", startMonth, endMonth);
            }
            if (!"all".equals(department)) {
                wrapper.eq("budget_item", department);
            }
            wrapper.orderByAsc("period", "budget_item");
            
            executions = budgetExecutionService.list(wrapper);
            
            // 计算汇总数据
            BigDecimal totalBudget = BigDecimal.ZERO;
            BigDecimal totalActual = BigDecimal.ZERO;
            BigDecimal remaining = BigDecimal.ZERO;
            BigDecimal overBudget = BigDecimal.ZERO;
            
            for (BudgetExecution exec : executions) {
                if (exec.getBudgetAmount() != null) {
                    totalBudget = totalBudget.add(exec.getBudgetAmount());
                }
                if (exec.getActualAmount() != null) {
                    totalActual = totalActual.add(exec.getActualAmount());
                    if (exec.getActualAmount().compareTo(exec.getBudgetAmount()) > 0) {
                        overBudget = overBudget.add(exec.getActualAmount().subtract(exec.getBudgetAmount()));
                    }
                }
            }
            
            remaining = totalBudget.subtract(totalActual);
            
            // 构建返回数据
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalBudget", String.format("%.2f", totalBudget.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP)));
            summary.put("totalActual", String.format("%.2f", totalActual.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP)));
            summary.put("remaining", String.format("%.2f", remaining.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP)));
            summary.put("overBudget", String.format("%.2f", overBudget.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP)));
            
            result.put("summary", summary);
            result.put("executions", executions);
            
            // 部门分布数据（按预算项目分组）
            Map<String, BigDecimal> departmentDistribution = new HashMap<>();
            for (BudgetExecution exec : executions) {
                String item = exec.getBudgetItem();
                if (item != null) {
                    departmentDistribution.putIfAbsent(item, BigDecimal.ZERO);
                    departmentDistribution.put(item, 
                        departmentDistribution.get(item).add(exec.getBudgetAmount()));
                }
            }
            
            result.put("departmentDistribution", departmentDistribution);
            
        } catch (Exception e) {
            e.printStackTrace();
            // 返回空数据
            result.put("summary", new HashMap<>());
            result.put("executions", new ArrayList<>());
            result.put("departmentDistribution", new HashMap<>());
        }
        
        return Result.success(result);
    }
}
