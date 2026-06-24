package com.example.finance.service;

import com.example.finance.entity.BudgetExecution;
import com.example.finance.mapper.BudgetExecutionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预算服务类（用于预算执行数据分析）
 */
@Service
public class BudgetService {

    @Autowired
    private BudgetExecutionMapper budgetExecutionMapper;

    /**
     * 获取预算执行数据
     * @param year 年份
     * @param item 预算项目（可选）
     * @return 预算执行列表
     */
    public List<BudgetExecution> getBudgetExecutionData(Integer year, String item) {
        if (item != null && !item.isEmpty()) {
            return budgetExecutionMapper.selectByYearAndItem(year, item);
        } else {
            return budgetExecutionMapper.selectByYear(year);
        }
    }

    /**
     * 获取部门预算汇总（用于图表）
     * @param year 年份
     * @return 预算执行列表
     */
    public List<BudgetExecution> getDepartmentBudget(Integer year) {
        // 这里可以按部门汇总，暂时返回年度数据
        return budgetExecutionMapper.selectByYear(year);
    }
}
