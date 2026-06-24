package com.example.finance.task;

import com.example.finance.entity.BudgetExecution;
import com.example.finance.entity.PurchaseOrder;
import com.example.finance.service.BudgetExecutionService;
import com.example.finance.service.PurchaseOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务组件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final PurchaseOrderService purchaseOrderService;
    private final BudgetExecutionService budgetExecutionService;

    /**
     * 数据采集任务 - 每 30 分钟执行一次
     * 实际业务中应该从 ERP、MES 等系统采集数据
     */
    @Scheduled(cron = "${schedule.data-collection}")
    public void dataCollectionTask() {
        log.info("========== 开始执行定时数据采集任务 ==========");
        
        try {
            // 统计今日订单数量和金额（使用 orderDate 字段）
            LocalDate today = LocalDate.now();
            LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PurchaseOrder::getOrderDate, today);
            long todayOrderCount = purchaseOrderService.count(wrapper);
            
            // 统计今日订单总金额
            List<PurchaseOrder> todayOrders = purchaseOrderService.list(wrapper);
            java.math.BigDecimal totalAmount = todayOrders.stream()
                .map(PurchaseOrder::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            log.info("数据采集完成，今日新增订单数：{}，总金额：{}", todayOrderCount, totalAmount);
            
            // TODO: 未来可以从外部系统采集更多数据
            // 1. 从 MES 系统采集生产数据
            // 2. 从 WMS 系统采集库存数据
            // 3. 从财务系统采集费用数据
        } catch (Exception e) {
            log.error("数据采集失败", e);
        }
        
        log.info("========== 数据采集任务执行完毕 ==========");
    }

    /**
     * 预算检查任务 - 每 15 分钟执行一次
     * 检查预算执行情况，超预算时发送预警
     */
    @Scheduled(cron = "${schedule.budget-check}")
    public void budgetCheckTask() {
        log.info("========== 开始执行预算检查任务 ==========");
        
        try {
            // 查询所有预算项目的执行情况
            List<BudgetExecution> allExecutions = budgetExecutionService.list();
            
            int warningCount = 0;
            for (BudgetExecution execution : allExecutions) {
                java.math.BigDecimal budgetAmount = execution.getBudgetAmount();
                java.math.BigDecimal actualAmount = execution.getActualAmount();
                
                if (budgetAmount != null && actualAmount != null && budgetAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    double executionRate = actualAmount.divide(budgetAmount, 4, java.math.BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
                    
                    // 超预算预警（执行率超过 100%）
                    if (executionRate > 100) {
                        log.warn("⚠️ 预瞽：{}({}) - 预算执行率 {:.2f}% (预算：{}, 实际：{})",
                            execution.getBudgetItem(), execution.getPeriod(), executionRate, budgetAmount, actualAmount);
                        warningCount++;
                    }
                    // 接近预算预警（执行率超过 80%）
                    else if (executionRate > 80) {
                        log.warn("⚠️ 注意：{}({}) - 预算执行率 {:.2f}% (预算：{}, 实际：{})",
                            execution.getBudgetItem(), execution.getPeriod(), executionRate, budgetAmount, actualAmount);
                    }
                }
            }
            
            if (warningCount > 0) {
                log.error("🚨 共发现 {} 个超预算项目，请及时处理！", warningCount);
            } else {
                log.info("预算检查完成，所有项目执行情况正常");
            }
            
            // TODO: 未来可以添加更多功能
            // 1. 发送预警通知给相关负责人
            // 2. 自动生成预警工单
            // 3. 记录预警日志到数据库
            
        } catch (Exception e) {
            log.error("预算检查失败", e);
        }
        
        log.info("========== 预算检查任务执行完毕 ==========");
    }

    /**
     * 趋势预测任务 - 每天凌晨 2 点执行
     * 基于历史数据进行趋势预测
     */
    @Scheduled(cron = "${schedule.trend-prediction}")
    public void predictionTask() {
        log.info("========== 开始执行趋势预测任务 ==========");
        
        try {
            // 简单的趋势分析 - 基于采购订单数据
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            LocalDate firstDayOfLastMonth = firstDayOfMonth.minusMonths(1);
            
            // 查询本月的订单数据
            LambdaQueryWrapper<PurchaseOrder> thisMonthWrapper = new LambdaQueryWrapper<>();
            thisMonthWrapper.ge(PurchaseOrder::getOrderDate, firstDayOfMonth);
            
            // 查询上月的订单数据
            LambdaQueryWrapper<PurchaseOrder> lastMonthWrapper = new LambdaQueryWrapper<>();
            lastMonthWrapper.between(PurchaseOrder::getOrderDate, firstDayOfLastMonth, firstDayOfMonth);
            
            List<PurchaseOrder> thisMonthOrders = purchaseOrderService.list(thisMonthWrapper);
            List<PurchaseOrder> lastMonthOrders = purchaseOrderService.list(lastMonthWrapper);
            
            // 计算订单数量趋势
            long thisMonthCount = thisMonthOrders.size();
            long lastMonthCount = lastMonthOrders.size();
            
            double growthRate = 0;
            if (lastMonthCount > 0) {
                growthRate = ((double)(thisMonthCount - lastMonthCount) / lastMonthCount) * 100;
            }
            
            String trend = growthRate > 0 ? "上升" : (growthRate < 0 ? "下降" : "持平");
            
            log.info("📊 采购趋势分析:");
            log.info("  - 本月订单数：{}，上月订单数：{}", thisMonthCount, lastMonthCount);
            log.info("  - 环比增长率：{:.2f}%, 趋势：{}", Math.abs(growthRate), trend);
            
            // 简单预测下月（仅基于当前趋势）
            long predictedNextMonth = (long)(thisMonthCount * (1 + growthRate / 100));
            log.info("  - 下月预测订单数：{} (仅供参考)", predictedNextMonth);
            
            // TODO: 未来可以使用更复杂的预测算法
            // 1. 移动平均法
            // 2. 指数平滑法
            // 3. 机器学习模型（LSTM、ARIMA 等）
            // 4. 考虑季节性因素
            
        } catch (Exception e) {
            log.error("趋势预测失败", e);
        }
        
        log.info("========== 趋势预测任务执行完毕 ==========");
    }
}
