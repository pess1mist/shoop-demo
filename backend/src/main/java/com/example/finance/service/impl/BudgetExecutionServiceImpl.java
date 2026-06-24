package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.BudgetExecution;
import com.example.finance.mapper.BudgetExecutionMapper;
import com.example.finance.service.BudgetExecutionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 预算执行服务实现类
 */
@Service
public class BudgetExecutionServiceImpl extends ServiceImpl<BudgetExecutionMapper, BudgetExecution> implements BudgetExecutionService {

    @Override
    public List<BudgetExecution> listAll() {
        return this.list();
    }

    @Override
    public List<BudgetExecution> listByPeriod(String period) {
        LambdaQueryWrapper<BudgetExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BudgetExecution::getPeriod, period);
        return this.list(wrapper);
    }

    @Override
    public List<BudgetExecution> listByBudgetItem(String budgetItem) {
        LambdaQueryWrapper<BudgetExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BudgetExecution::getBudgetItem, budgetItem);
        return this.list(wrapper);
    }

    @Override
    public String analyzeVariance(String budgetItem) {
        List<BudgetExecution> budgetList = listByBudgetItem(budgetItem);
        if (budgetList.isEmpty()) {
            return "暂无预算数据";
        }

        BigDecimal totalBudget = budgetList.stream()
                .map(BudgetExecution::getBudgetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActual = budgetList.stream()
                .map(BudgetExecution::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVariance = totalActual.subtract(totalBudget);
        BigDecimal varianceRate = totalBudget.compareTo(BigDecimal.ZERO) != 0 ?
                totalVariance.divide(totalBudget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;

        String status = totalVariance.compareTo(BigDecimal.ZERO) > 0 ? "超支" : "节约";

        return String.format("预算项目%s：预算总额%.2f 元，实际总额%.2f 元，差异%.2f 元，差异率%.2f%%，状态：%s",
                budgetItem, totalBudget, totalActual, totalVariance, varianceRate, status);
    }

    @Override
    public List<BudgetExecution> listByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<BudgetExecution> wrapper = new LambdaQueryWrapper<>();
        
        // 动态添加查询条件
        if (params != null) {
            // 预算项目条件
            String budgetItem = (String) params.get("budgetItem");
            if (StringUtils.hasText(budgetItem) && !"all".equals(budgetItem)) {
                wrapper.eq(BudgetExecution::getBudgetItem, budgetItem);
            }
            
            // 期间条件（月份范围）
            String monthRange = (String) params.get("monthRange");
            if (StringUtils.hasText(monthRange) && !"all".equals(monthRange)) {
                // 根据前端定义的格式：近 3 月、近 6 月、近 12 月，这里简化处理
                wrapper.like(BudgetExecution::getPeriod, monthRange);
            }
        }
        
        // 按期间降序排列
        wrapper.orderByDesc(BudgetExecution::getPeriod);
        
        return this.list(wrapper);
    }
}
