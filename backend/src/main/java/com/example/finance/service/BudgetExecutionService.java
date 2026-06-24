package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.BudgetExecution;

import java.util.List;

/**
 * 预算执行服务接口
 */
public interface BudgetExecutionService extends IService<BudgetExecution> {

    /**
     * 查询所有预算执行
     */
    List<BudgetExecution> listAll();

    /**
     * 查询某期间的预算执行
     * @param period 期间（yyyy-MM）
     * @return 预算执行列表
     */
    List<BudgetExecution> listByPeriod(String period);

    /**
     * 查询某预算项目的执行情况
     * @param budgetItem 预算项目
     * @return 预算执行列表
     */
    List<BudgetExecution> listByBudgetItem(String budgetItem);

    /**
     * 分析预算执行差异
     * @param budgetItem 预算项目
     * @return 差异分析报告
     */
    String analyzeVariance(String budgetItem);

    /**
     * 根据条件动态查询预算执行
     * @param params 查询条件（department: 部门，monthRange: 月份范围）
     * @return 预算执行列表
     */
    List<BudgetExecution> listByCondition(java.util.Map<String, Object> params);
}
