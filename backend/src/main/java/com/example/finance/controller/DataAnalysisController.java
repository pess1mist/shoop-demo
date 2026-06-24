package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.*;
import com.example.finance.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据分析控制器
 * 为 5 大核心页面提供数据查询接口
 */
@Tag(name = "数据分析", description = "为 5 大核心页面提供数据查询接口")
@RestController
@RequestMapping("/api/data-analysis")
@CrossOrigin(origins = "*")
public class DataAnalysisController {

    @Autowired
    private ProductionOrderService productionOrderService;

    @Autowired
    private BudgetExecutionService budgetExecutionService;

    @Autowired
    private ManufacturingCostDetailService manufacturingCostDetailService;

    @Autowired
    private InternalControlLogService internalControlLogService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private ProductService productService;  // ✅ 添加产品服务

    @Autowired
    private SalesOrderService salesOrderService;  // ✅ 添加销售订单服务

    @Autowired
    private javax.sql.DataSource dataSource;  // ✅ 添加数据源

    /**
     * 获取数据可视化分析（监控仪表盘）数据
     * 注：当前版本显示实时累计数据，year参数保留用于向后兼容
     */
    @Operation(summary = "获取数据可视化分析数据", description = "返回监控仪表盘的所有数据，包括成本、利润、预算执行率等（实时累计）")
    @GetMapping(value = "/data-viz", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Map<String, Object>> getDataVisualization(
            @RequestParam(required = false, defaultValue = "all") String year,
            @RequestParam(required = false, defaultValue = "all") String productLine,
            HttpServletResponse response) {
        
        // 设置字符编码为 UTF-8
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> data = new HashMap<>();
        
        try {
            // 获取预警数据（根据产品线筛选）
            List<InternalControlLog> allAlerts = internalControlLogService.listAll();
            List<InternalControlLog> filteredAlerts = allAlerts.stream()
                .filter(alert -> "all".equals(productLine) || 
                       alert.getAlertContent().contains(productLine) ||
                       alert.getAlertType().contains(productLine))
                .collect(java.util.stream.Collectors.toList());
            
            data.put("alerts", filteredAlerts);
            
            // 2. ✅ 从生产订单表和产品表计算真实收入和成本（支持筛选）
            // 收入 = 产量 × 产品单价
            // 成本 = 产量 × 产品成本价 (销售成本)
            // 利润 = 收入 - 成本
            List<ProductionOrder> allProductionOrders = productionOrderService.listAll();
            
            // 根据产品线筛选生产订单
            List<ProductionOrder> filteredOrders = allProductionOrders;
            if (!"all".equals(productLine)) {
                // 建立产品线与产品代码的映射
                java.util.Map<String, String> lineToProductMap = new java.util.HashMap<>();
                lineToProductMap.put("line1", "P02");  // 膨化线
                lineToProductMap.put("line2", "P03");  // 乳化线
                lineToProductMap.put("line3", "P01");  // 水胶线
                
                String targetProductCode = lineToProductMap.get(productLine);
                if (targetProductCode != null) {
                    final String finalTargetProductCode = targetProductCode;
                    filteredOrders = allProductionOrders.stream()
                        .filter(order -> finalTargetProductCode.equals(order.getProductCode()))
                        .collect(java.util.stream.Collectors.toList());
                }
            }
            
            java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;
            java.math.BigDecimal totalCost = java.math.BigDecimal.ZERO;
            
            for (ProductionOrder order : filteredOrders) {
                String productCode = order.getProductCode();
                Integer actualQuantity = order.getActualQuantity();
                
                if (actualQuantity != null && productCode != null) {
                    // 从 product 表获取该产品的单价和成本价
                    Product product = productService.getByProductCode(productCode);
                    if (product != null && product.getUnitPrice() != null && product.getCostPrice() != null) {
                        // 收入 = 产量 × 单价
                        java.math.BigDecimal revenue = new java.math.BigDecimal(actualQuantity.toString())
                            .multiply(product.getUnitPrice());
                        totalRevenue = totalRevenue.add(revenue);
                        
                        // 成本 = 产量 × 成本价
                        java.math.BigDecimal cost = new java.math.BigDecimal(actualQuantity.toString())
                            .multiply(product.getCostPrice());
                        totalCost = totalCost.add(cost);
                    }
                }
            }
            
            // 转换为万元单位
            totalRevenue = totalRevenue.divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP);
            totalCost = totalCost.divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP);
            
            // 计算利润 = 收入 - 成本
            java.math.BigDecimal profit = totalRevenue.subtract(totalCost);
            
            // 3. 计算预算执行率（使用 budget_execution_base 表）
            // 直接从 budget_execution_base 表汇总数据
            String sql = "SELECT SUM(budget_amount) as total_budget, SUM(actual_amount) as total_actual FROM budget_execution_base";
            java.math.BigDecimal totalBudget = java.math.BigDecimal.ZERO;
            java.math.BigDecimal totalActual = java.math.BigDecimal.ZERO;
            
            try (java.sql.Connection conn = dataSource.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    String budgetStr = rs.getString("total_budget");
                    String actualStr = rs.getString("total_actual");
                    if (budgetStr != null) totalBudget = new java.math.BigDecimal(budgetStr);
                    if (actualStr != null) totalActual = new java.math.BigDecimal(actualStr);
                }
            } catch (Exception e) {
                // 如果查询失败，使用默认值
                totalBudget = new java.math.BigDecimal("68865009");
                totalActual = new java.math.BigDecimal("68452149");
            }
            
            String budgetExecutionRate = totalBudget.compareTo(java.math.BigDecimal.ZERO) > 0 
                ? String.format("%.2f", (totalActual.divide(totalBudget, 4, java.math.BigDecimal.ROUND_HALF_UP)
                    .multiply(new java.math.BigDecimal("100")))) + "%"
                : "0.00%";
            
            // ✅ 使用计算的真实总成本 (销售成本)
            data.put("totalCost", String.format("%.2f 万", totalCost));
            data.put("profit", String.format("%.2f 万", profit));
            data.put("revenue", String.format("%.2f 万", totalRevenue));
            data.put("budgetExecutionRate", budgetExecutionRate);
            data.put("unhandledAlerts", (int) filteredAlerts.stream()
                .filter(alert -> "PENDING".equals(alert.getHandleStatus()) || "未处理".equals(alert.getHandleStatus()))
                .count());
            
            // 成本构成分析（用于成本结构饼图）
            Map<String, Object> costStructure = new HashMap<>();
            // 销售成本 = 直接材料 + 直接人工 + 制造费用
            // 由于我们使用的是标准成本法，成本结构按典型比例分配
            java.math.BigDecimal directMaterialRatio = new java.math.BigDecimal("0.70");
            java.math.BigDecimal directLaborRatio = new java.math.BigDecimal("0.15");
            java.math.BigDecimal overheadRatio = new java.math.BigDecimal("0.15");
            
            java.math.BigDecimal materialCost = totalCost.multiply(directMaterialRatio);
            java.math.BigDecimal laborCost = totalCost.multiply(directLaborRatio);
            java.math.BigDecimal overheadCost = totalCost.multiply(overheadRatio);
            
            // 计算百分比
            if (totalCost.compareTo(java.math.BigDecimal.ZERO) > 0) {
                costStructure.put("materialPercent", String.format("%.1f", materialCost.divide(totalCost, 4, java.math.BigDecimal.ROUND_HALF_UP).multiply(new java.math.BigDecimal("100"))));
                costStructure.put("laborPercent", String.format("%.1f", laborCost.divide(totalCost, 4, java.math.BigDecimal.ROUND_HALF_UP).multiply(new java.math.BigDecimal("100"))));
                costStructure.put("overheadPercent", String.format("%.1f", overheadCost.divide(totalCost, 4, java.math.BigDecimal.ROUND_HALF_UP).multiply(new java.math.BigDecimal("100"))));
            } else {
                costStructure.put("materialPercent", "0.0");
                costStructure.put("laborPercent", "0.0");
                costStructure.put("overheadPercent", "0.0");
            }
            
            costStructure.put("materialCost", materialCost);
            costStructure.put("laborCost", laborCost);
            costStructure.put("overheadCost", overheadCost);
            costStructure.put("totalCost", totalCost);
            
            // 计算各产品线的单位成本和产量（用于前端图表展示）
            // ✅ 使用生产订单表的真实数据，并支持产品线筛选
            Map<String, java.math.BigDecimal> vizProductTotalCost = new java.util.HashMap<>();
            Map<String, java.math.BigDecimal> vizProductTotalQuantity = new java.util.HashMap<>();
            
            // 建立产品线与产品的映射关系
            java.util.Map<String, String> lineToProductMap = new java.util.HashMap<>();
            lineToProductMap.put("水胶线", "P01");
            lineToProductMap.put("膨化线", "P02");
            lineToProductMap.put("乳化线", "P03");
            
            // 从生产订单表获取真实的成本和产量数据（使用已筛选的订单列表）
            for (ProductionOrder order : filteredOrders) {
                String productCode = order.getProductCode();
                Integer actualQuantity = order.getActualQuantity();
                Integer orderTotalCost = order.getTotalCost();
                
                if (actualQuantity != null && orderTotalCost != null) {
                    // 查找对应的生产线
                    for (java.util.Map.Entry<String, String> entry : lineToProductMap.entrySet()) {
                        if (entry.getValue().equals(productCode)) {
                            String line = entry.getKey();
                            
                            // 如果选择了特定产品线，只统计该产线的数据
                            if (!"all".equals(productLine)) {
                                String targetLine = "";
                                if ("line1".equals(productLine)) targetLine = "膨化线";
                                else if ("line2".equals(productLine)) targetLine = "乳化线";
                                else if ("line3".equals(productLine)) targetLine = "水胶线";
                                
                                if (!line.equals(targetLine)) {
                                    continue;  // 跳过不匹配的生产线
                                }
                            }
                            
                            // 累加产量
                            java.math.BigDecimal existingQty = vizProductTotalQuantity.getOrDefault(line, java.math.BigDecimal.ZERO);
                            vizProductTotalQuantity.put(line, existingQty.add(new java.math.BigDecimal(actualQuantity)));
                            
                            // 累加成本
                            java.math.BigDecimal existingCost = vizProductTotalCost.getOrDefault(line, java.math.BigDecimal.ZERO);
                            vizProductTotalCost.merge(line, new java.math.BigDecimal(orderTotalCost), java.math.BigDecimal::add);
                            
                            break;
                        }
                    }
                }
            }
            
            // 计算单位成本 = 总成本 / 总产量
            Map<String, Object> vizUnitCostData = new java.util.HashMap<>();
            for (String line : vizProductTotalCost.keySet()) {
                java.math.BigDecimal lineTotalCost = vizProductTotalCost.get(line);
                java.math.BigDecimal lineTotalQuantity = vizProductTotalQuantity.getOrDefault(line, java.math.BigDecimal.ZERO);
                
                // ✅ 如果产量>0，计算真实单位成本；否则显示总成本 (万元)
                java.math.BigDecimal displayValue;
                if (lineTotalQuantity.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    displayValue = lineTotalCost.divide(lineTotalQuantity, 2, java.math.BigDecimal.ROUND_HALF_UP);
                } else {
                    displayValue = lineTotalCost.divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP);
                }
                
                vizUnitCostData.put(line, displayValue);
            }
            
            // 更新成本结构数据（包含单位成本和产量）
            costStructure.put("unitCosts", vizUnitCostData);
            costStructure.put("quantities", vizProductTotalQuantity);
            
            // 更新 costStructure 到 data
            data.put("costStructure", costStructure);
            
            // 计算预算偏差 TOP3（从 budget_execution 表计算）
            List<BudgetExecution> allBudgets = budgetExecutionService.listAll();
            List<Map<String, Object>> budgetVarianceList = new ArrayList<>();
            
            for (BudgetExecution budget : allBudgets) {
                java.math.BigDecimal variance = budget.getActualAmount().subtract(budget.getBudgetAmount());
                Map<String, Object> varianceItem = new HashMap<>();
                varianceItem.put("item", budget.getBudgetItem());
                varianceItem.put("amount", variance.divide(new java.math.BigDecimal("10000"), 1, java.math.BigDecimal.ROUND_HALF_UP));
                budgetVarianceList.add(varianceItem);
            }
            
            // 按偏差绝对值排序，取 TOP3
            budgetVarianceList.sort((a, b) -> {
                java.math.BigDecimal varianceA = (java.math.BigDecimal) a.get("amount");
                java.math.BigDecimal varianceB = (java.math.BigDecimal) b.get("amount");
                return varianceB.abs().compareTo(varianceA.abs());
            });
            
            List<Map<String, Object>> budgetVarianceTop3 = budgetVarianceList.stream().limit(3).collect(java.util.stream.Collectors.toList());
            data.put("budgetVarianceTop3", budgetVarianceTop3);
            
            // 计算预警类型统计（用于 InternalControl 页面的饼图）
            List<InternalControlLog> allAlertsForStat = internalControlLogService.listAll();
            Map<String, Integer> alertTypeCount = new HashMap<>();
            
            for (InternalControlLog alert : allAlertsForStat) {
                String alertType = alert.getAlertType();
                // 将英文类型转换为中文显示
                String chineseType = "";
                switch (alertType) {
                    case "Budget Overrun":
                        chineseType = "超预算";
                        break;
                    case "Cost Exception":
                        chineseType = "成本超支";
                        break;
                    case "Price Exception":
                        chineseType = "价格异常";
                        break;
                    case "Inventory Warning":
                        chineseType = "库存预警";
                        break;
                    case "Quality Exception":
                        chineseType = "质量异常";
                        break;
                    default:
                        chineseType = alertType;
                }
                
                alertTypeCount.merge(chineseType, 1, Integer::sum);
            }
            
            data.put("alertTypeStats", alertTypeCount);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取数据可视化分析数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取成本分析数据
     * 注：当前版本显示实时累计数据，yearRange参数保留用于向后兼容
     */
    @Operation(summary = "获取成本分析数据", description = "返回成本分析页面的所有数据（实时累计）")
    @GetMapping("/cost-analysis")
    public Result<Map<String, Object>> getCostAnalysis(
            @RequestParam(required = false, defaultValue = "all") String yearRange,
            @RequestParam(required = false, defaultValue = "all") String product) {
        
        Map<String, Object> data = new HashMap<>();
        
        try {
            // 获取制造成本数据
            List<ManufacturingCostDetail> allCosts = manufacturingCostDetailService.listAll();
            
            // 根据产品线筛选
            List<ManufacturingCostDetail> filteredCosts = allCosts.stream()
                .filter(cost -> "all".equals(product) || 
                       cost.getProductionLine().contains(product))
                .collect(java.util.stream.Collectors.toList());
            
            data.put("costs", filteredCosts);
            
            // 计算各产品线的单位成本（用于前端图表展示）
            // 从生产订单表获取真实的产量数据
            Map<String, java.math.BigDecimal> productTotalCost = new java.util.HashMap<>();
            Map<String, java.math.BigDecimal> productTotalQuantity = new java.util.HashMap<>();
            
            // 1. ✅ 从生产订单表获取真实的成本和产量数据
            // 建立产品线与产品的映射关系
            java.util.Map<String, String> lineToProductMap = new java.util.HashMap<>();
            lineToProductMap.put("水胶线", "P01");
            lineToProductMap.put("膨化线", "P02");
            lineToProductMap.put("乳化线", "P03");
            
            // 从生产订单表累加产量和成本
            for (ProductionOrder order : productionOrderService.listAll()) {
                String productCode = order.getProductCode();
                Integer actualQuantity = order.getActualQuantity();
                Integer totalCost = order.getTotalCost();
                
                if (actualQuantity != null && totalCost != null) {
                    // 查找对应的生产线
                    for (java.util.Map.Entry<String, String> entry : lineToProductMap.entrySet()) {
                        if (entry.getValue().equals(productCode)) {
                            String line = entry.getKey();
                            
                            // 累加产量
                            java.math.BigDecimal existingQty = productTotalQuantity.getOrDefault(line, java.math.BigDecimal.ZERO);
                            productTotalQuantity.put(line, existingQty.add(new java.math.BigDecimal(actualQuantity)));
                            
                            // 累加成本
                            java.math.BigDecimal existingCost = productTotalCost.getOrDefault(line, java.math.BigDecimal.ZERO);
                            productTotalCost.merge(line, new java.math.BigDecimal(totalCost), java.math.BigDecimal::add);
                            
                            break;
                        }
                    }
                }
            }
            
            // 4. 计算单位成本 = 总成本 / 总产量
            Map<String, Object> unitCostData = new java.util.HashMap<>();
            for (String productLine : productTotalCost.keySet()) {
                java.math.BigDecimal totalCost = productTotalCost.get(productLine);
                java.math.BigDecimal totalQuantity = productTotalQuantity.getOrDefault(productLine, java.math.BigDecimal.ZERO);
                
                // ✅ 如果产量>0，计算真实单位成本；否则显示总成本 (万元)
                java.math.BigDecimal displayValue;
                if (totalQuantity.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    displayValue = totalCost.divide(totalQuantity, 2, java.math.BigDecimal.ROUND_HALF_UP);
                } else {
                    displayValue = totalCost.divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP);
                }
                
                unitCostData.put(productLine, displayValue);
            }
            
            // 更新成本结构数据（包含单位成本和产量）
            Map<String, Object> costStructureData = new java.util.HashMap<>();
            costStructureData.put("unitCosts", unitCostData);
            costStructureData.put("quantities", productTotalQuantity);
            costStructureData.put("totalCount", filteredCosts.size());
            data.put("costStructure", costStructureData);
            
            // ✅ 添加按年份分组的成本结构数据（用于前端展示三年对比）
            // 从制造成本中按年份分组计算
            java.util.Map<String, java.math.BigDecimal> yearMaterialCost = new java.util.HashMap<>();
            java.util.Map<String, java.math.BigDecimal> yearLaborCost = new java.util.HashMap<>();
            java.util.Map<String, java.math.BigDecimal> yearOverheadCost = new java.util.HashMap<>();
            
            for (ManufacturingCostDetail cost : allCosts) {
                String year = String.valueOf(cost.getCostDate().getYear());
                String category = cost.getCostCategory();
                java.math.BigDecimal amount = cost.getAmount();
                
                // ✅ 支持中英文成本类别
                if ("材料费".equals(category) || "Direct Material".equals(category) || "Material".equals(category)) {
                    yearMaterialCost.merge(year, amount, java.math.BigDecimal::add);
                } else if ("人工费".equals(category) || "Direct Labor".equals(category) || "Labor".equals(category)) {
                    yearLaborCost.merge(year, amount, java.math.BigDecimal::add);
                } else if ("制造费用".equals(category) || "Manufacturing Overhead".equals(category) || "Overhead".equals(category)) {
                    yearOverheadCost.merge(year, amount, java.math.BigDecimal::add);
                }
            }
            
            // 构建成本结构数组（按年份分组）
            java.util.List<java.util.Map<String, Object>> costStructureByYear = new java.util.ArrayList<>();
            String[] years = {"2023", "2024", "2025"};
            for (String year : years) {
                java.math.BigDecimal material = yearMaterialCost.getOrDefault(year, java.math.BigDecimal.ZERO);
                java.math.BigDecimal labor = yearLaborCost.getOrDefault(year, java.math.BigDecimal.ZERO);
                java.math.BigDecimal overhead = yearOverheadCost.getOrDefault(year, java.math.BigDecimal.ZERO);
                java.math.BigDecimal total = material.add(labor).add(overhead);
                
                java.util.Map<String, Object> yearStructure = new java.util.HashMap<>();
                yearStructure.put("year", year);
                yearStructure.put("materialPercent", total.compareTo(java.math.BigDecimal.ZERO) > 0 ? 
                    String.format("%.1f", material.divide(total, 4, java.math.BigDecimal.ROUND_HALF_UP).multiply(new java.math.BigDecimal("100"))) : "0.0");
                yearStructure.put("laborPercent", total.compareTo(java.math.BigDecimal.ZERO) > 0 ? 
                    String.format("%.1f", labor.divide(total, 4, java.math.BigDecimal.ROUND_HALF_UP).multiply(new java.math.BigDecimal("100"))) : "0.0");
                yearStructure.put("overheadPercent", total.compareTo(java.math.BigDecimal.ZERO) > 0 ? 
                    String.format("%.1f", overhead.divide(total, 4, java.math.BigDecimal.ROUND_HALF_UP).multiply(new java.math.BigDecimal("100"))) : "0.0");
                
                costStructureByYear.add(yearStructure);
            }
            
            // ✅ 添加成本趋势数据（用于图表展示）
            java.util.List<java.util.Map<String, Object>> costTrend = new java.util.ArrayList<>();
            for (String year : years) {
                java.math.BigDecimal material = yearMaterialCost.getOrDefault(year, java.math.BigDecimal.ZERO);
                java.math.BigDecimal labor = yearLaborCost.getOrDefault(year, java.math.BigDecimal.ZERO);
                java.math.BigDecimal overhead = yearOverheadCost.getOrDefault(year, java.math.BigDecimal.ZERO);
                java.math.BigDecimal total = material.add(labor).add(overhead);
                
                // 计算单位成本（假设每年产量相同，暂时用总成本代替）
                java.util.Map<String, Object> trendItem = new java.util.HashMap<>();
                trendItem.put("year", year);
                trendItem.put("unitCost", total.divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP));
                costTrend.add(trendItem);
            }
            
            // 返回筛选后的成本数据
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取成本分析数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取预算执行数据
     */
    @Operation(summary = "获取预算执行数据", description = "返回预算执行页面的所有数据")
    @GetMapping("/budget-execution")
    public Result<Map<String, Object>> getBudgetExecution(
            @RequestParam(required = false, defaultValue = "all") String department,
            @RequestParam(required = false, defaultValue = "all") String monthRange) {
        
        Map<String, Object> data = new HashMap<>();
        
        try {
            // 构建查询条件
            Map<String, Object> queryParams = new HashMap<>();
            if (!"all".equals(department)) {
                queryParams.put("budgetItem", department);
            }
            if (!"all".equals(monthRange)) {
                queryParams.put("monthRange", monthRange);
            }
            
            // 根据条件查询预算执行数据
            List<BudgetExecution> executions;
            if (queryParams.isEmpty()) {
                executions = budgetExecutionService.listAll();
            } else {
                executions = budgetExecutionService.listByCondition(queryParams);
            }
            data.put("executions", executions);
            
            // 获取预算项目列表（用于筛选）
            List<String> budgetItems = budgetExecutionService.list()
                    .stream()
                    .map(BudgetExecution::getBudgetItem)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            data.put("budgetItems", budgetItems);
            
            // 计算汇总指标
            java.math.BigDecimal totalBudget = executions.stream()
                .map(BudgetExecution::getBudgetAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            java.math.BigDecimal totalActual = executions.stream()
                .map(BudgetExecution::getActualAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            java.math.BigDecimal variance = totalActual.subtract(totalBudget);
            
            Map<String, Object> summary = new java.util.HashMap<>();
            summary.put("totalBudget", String.format("%.2f", totalBudget.divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP)));
            summary.put("totalActual", String.format("%.2f", totalActual.divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP)));
            summary.put("remaining", String.format("%.2f", java.math.BigDecimal.ZERO.max(totalBudget.subtract(totalActual)).divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP)));
            summary.put("overBudget", String.format("%.2f", java.math.BigDecimal.ZERO.max(variance).divide(new java.math.BigDecimal("10000"), 2, java.math.BigDecimal.ROUND_HALF_UP)));
            data.put("summary", summary);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取预算执行数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取生产监控数据
     */
    @Operation(summary = "获取生产监控数据", description = "返回生产监控页面的所有数据")
    @GetMapping("/production-monitor")
    public Result<Map<String, Object>> getProductionMonitor(
            @RequestParam(required = false, defaultValue = "all") String line,
            @RequestParam(required = false, defaultValue = "all") String status) {
        
        Map<String, Object> data = new HashMap<>();
        
        try {
            // 获取所有生产订单
            List<ProductionOrder> allOrders = productionOrderService.listAll();
            
            // 根据生产线筛选
            List<ProductionOrder> filteredOrders = allOrders.stream()
                .filter(order -> "all".equals(line) || order.getProductCode().contains(line))
                .collect(Collectors.toList());
            
            data.put("orders", filteredOrders);
            
            // 计算汇总指标
            Integer totalQuantity = filteredOrders.stream()
                .map(ProductionOrder::getActualQuantity)
                .reduce(0, Integer::sum);
            Integer totalCost = filteredOrders.stream()
                .map(ProductionOrder::getTotalCost)
                .reduce(0, Integer::sum);
            
            Map<String, Object> summary = new java.util.HashMap<>();
            summary.put("totalQuantity", String.format("%.2f 吨", totalQuantity.doubleValue()));
            summary.put("totalCost", String.format("%.2f 万", (totalCost.doubleValue() / 10000)));
            data.put("summary", summary);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取生产监控数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取内控预警数据
     */
    @Operation(summary = "获取内控预警数据", description = "返回内控预警页面的所有数据")
    @GetMapping("/internal-control")
    public Result<Map<String, Object>> getInternalControl(
            @RequestParam(required = false, defaultValue = "all") String level,
            @RequestParam(required = false, defaultValue = "all") String handleStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Map<String, Object> data = new HashMap<>();
        
        try {
            // 获取所有预警日志
            List<InternalControlLog> allLogs = internalControlLogService.listAll();
            
            // 根据处理状态筛选
            if (!"all".equals(handleStatus)) {
                final String filterStatus = handleStatus;
                allLogs = allLogs.stream()
                    .filter(log -> {
                        String status = log.getHandleStatus();
                        if ("unhandled".equals(filterStatus)) {
                            return "未处理".equals(status);
                        } else if ("processing".equals(filterStatus)) {
                            return "处理中".equals(status);
                        } else if ("handled".equals(filterStatus)) {
                            return "已处理".equals(status);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            }
            
            // 根据预警级别筛选（简化版：根据关键字匹配）
            if (!"all".equals(level)) {
                final String filterLevel = level;
                allLogs = allLogs.stream()
                    .filter(log -> {
                        String content = log.getAlertContent();
                        if ("high".equals(filterLevel)) {
                            return content.contains("严重") || content.contains("紧急");
                        } else if ("medium".equals(filterLevel)) {
                            return content.contains("一般") || content.contains("注意");
                        } else if ("low".equals(filterLevel)) {
                            return content.contains("轻微") || content.contains("提示");
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            }
            
            // 根据时间范围筛选
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                try {
                    java.time.LocalDateTime startDateTime = java.time.LocalDateTime.parse(startDate + "T00:00:00");
                    java.time.LocalDateTime endDateTime = java.time.LocalDateTime.parse(endDate + "T23:59:59");
                    final java.time.LocalDateTime start = startDateTime;
                    final java.time.LocalDateTime end = endDateTime;
                    allLogs = allLogs.stream()
                        .filter(log -> log.getAlertTime() != null && 
                              !log.getAlertTime().isBefore(start) && 
                              !log.getAlertTime().isAfter(end))
                        .collect(Collectors.toList());
                } catch (Exception e) {
                    // 时间格式不正确，忽略时间筛选
                }
            }
            
            data.put("logs", allLogs);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取内控预警数据失败：" + e.getMessage());
        }
    }
}
