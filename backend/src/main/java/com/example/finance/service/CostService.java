package com.example.finance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.finance.entity.ManufacturingCostDetail;
import com.example.finance.entity.Product;
import com.example.finance.entity.ProductionOrder;
import com.example.finance.entity.PurchasePriceHistory;
import com.example.finance.mapper.ManufacturingCostDetailMapper;
import com.example.finance.mapper.ProductMapper;
import com.example.finance.mapper.ProductionOrderMapper;
import com.example.finance.mapper.PurchasePriceHistoryMapper;
import com.example.finance.vo.CostStructureVO;
import com.example.finance.vo.CostTrendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 成本服务类（用于成本趋势分析）
 */
@Service
public class CostService {

    @Autowired
    private ProductionOrderMapper productionOrderMapper;

    @Autowired
    private PurchasePriceHistoryMapper purchasePriceHistoryMapper;

    @Autowired
    private ManufacturingCostDetailMapper manufacturingCostDetailMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取成本趋势数据
     * @param year 年份
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param productCode 产品编码（可选）
     * @return 成本趋势列表
     */
    public List<CostTrendVO> getCostTrend(Integer year, String startDate, String endDate, String productCode) {
        List<CostTrendVO> result = new ArrayList<>();

        if (year != null) {
            result = getCostTrendByYear(year, productCode);
        } else if (startDate != null && endDate != null) {
            result = getCostTrendByDateRange(startDate, endDate, productCode);
        } else {
            // ✅ 如果没有指定时间范围，查询所有历史数据（实时累计）
            result = getCostTrendByDateRange(null, null, productCode);
        }

        return result;
    }

    /**
     * 按年份获取成本趋势
     */
    private List<CostTrendVO> getCostTrendByYear(Integer year, String productCode) {
        List<CostTrendVO> result = new ArrayList<>();

        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("YEAR(end_date) = {0}", year);
        if (productCode != null && !productCode.isEmpty()) {
            wrapper.eq(ProductionOrder::getProductCode, productCode);
        }
        wrapper.orderByAsc(ProductionOrder::getEndDate);

        List<ProductionOrder> orders = productionOrderMapper.selectList(wrapper);

        Map<String, BigDecimal> monthlyCostMap = new TreeMap<>();
        Map<String, Integer> monthlyQuantityMap = new TreeMap<>();

        for (ProductionOrder order : orders) {
            String month = order.getEndDate().toString().substring(0, 7);
            BigDecimal cost = order.getTotalCost() != null ? new BigDecimal(order.getTotalCost().toString()) : BigDecimal.ZERO;
            Integer quantity = order.getActualQuantity() != null ? order.getActualQuantity() : 0;

            monthlyCostMap.merge(month, cost, BigDecimal::add);
            monthlyQuantityMap.merge(month, quantity, Integer::sum);
        }

        for (Map.Entry<String, BigDecimal> entry : monthlyCostMap.entrySet()) {
            String month = entry.getKey();
            BigDecimal totalCost = entry.getValue();
            Integer totalQuantity = monthlyQuantityMap.get(month);

            CostTrendVO vo = new CostTrendVO();
            vo.setYear(year);
            vo.setMonth(month);
            vo.setCost(totalCost);

            if (totalQuantity != null && totalQuantity > 0) {
                vo.setUnitCost(totalCost.divide(new BigDecimal(totalQuantity), 2, RoundingMode.HALF_UP));
            } else {
                vo.setUnitCost(totalCost);
            }

            if (productCode != null) {
                Product product = productMapper.selectOne(
                    new LambdaQueryWrapper<Product>().eq(Product::getProductCode, productCode)
                );
                if (product != null) {
                    vo.setProductCode(productCode);
                    vo.setProductName(product.getProductName());
                }
            }

            result.add(vo);
        }

        return result;
    }

    /**
     * 按日期范围获取成本趋势
     */
    private List<CostTrendVO> getCostTrendByDateRange(String startDate, String endDate, String productCode) {
        List<CostTrendVO> result = new ArrayList<>();

        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        // ✅ 如果 startDate 和 endDate 都为 null，查询所有数据
        if (startDate != null && endDate != null) {
            wrapper.between(ProductionOrder::getEndDate, startDate, endDate);
        }
        if (productCode != null && !productCode.isEmpty()) {
            wrapper.eq(ProductionOrder::getProductCode, productCode);
        }
        wrapper.orderByAsc(ProductionOrder::getEndDate);

        List<ProductionOrder> orders = productionOrderMapper.selectList(wrapper);

        // ✅ 按月份汇总数据
        Map<String, BigDecimal> monthlyCostMap = new TreeMap<>();
        Map<String, Integer> monthlyQuantityMap = new TreeMap<>();

        for (ProductionOrder order : orders) {
            String month = order.getEndDate().toString().substring(0, 7); // yyyy-MM
            BigDecimal cost = order.getTotalCost() != null ? new BigDecimal(order.getTotalCost().toString()) : BigDecimal.ZERO;
            Integer quantity = order.getActualQuantity() != null ? order.getActualQuantity() : 0;

            monthlyCostMap.merge(month, cost, BigDecimal::add);
            monthlyQuantityMap.merge(month, quantity, Integer::sum);
        }

        for (Map.Entry<String, BigDecimal> entry : monthlyCostMap.entrySet()) {
            String month = entry.getKey();
            BigDecimal totalCost = entry.getValue();
            Integer totalQuantity = monthlyQuantityMap.get(month);
            
            // 提取年份
            Integer year = Integer.parseInt(month.substring(0, 4));

            CostTrendVO vo = new CostTrendVO();
            vo.setYear(year);
            vo.setMonth(month);
            vo.setCost(totalCost);

            if (totalQuantity != null && totalQuantity > 0) {
                vo.setUnitCost(totalCost.divide(new BigDecimal(totalQuantity), 2, RoundingMode.HALF_UP));
            } else {
                vo.setUnitCost(totalCost);
            }

            if (productCode != null) {
                Product product = productMapper.selectOne(
                    new LambdaQueryWrapper<Product>().eq(Product::getProductCode, productCode)
                );
                if (product != null) {
                    vo.setProductCode(productCode);
                    vo.setProductName(product.getProductName());
                }
            }

            result.add(vo);
        }

        return result;
    }

    /**
     * 获取成本结构（按月度分组）- 用于堆叠柱状图
     * 注：如果没有指定 yearRange，默认查询所有历史数据（实时累计）
     */
    public List<CostStructureVO> getCostStructure(String yearRange, String product) {
        List<CostStructureVO> result = new ArrayList<>();

        // ✅ 按月度聚合成本数据
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        if (product != null && !product.isEmpty() && !"all".equals(product)) {
            wrapper.eq(ProductionOrder::getProductCode, product);
        }
        wrapper.isNotNull(ProductionOrder::getEndDate);
        wrapper.orderByAsc(ProductionOrder::getEndDate);

        List<ProductionOrder> orders = productionOrderMapper.selectList(wrapper);

        // 按年月分组
        Map<String, List<ProductionOrder>> groupedByMonth = orders.stream()
            .filter(order -> order.getEndDate() != null)
            .collect(Collectors.groupingBy(order -> 
                order.getEndDate().getYear() + "-" + String.format("%02d", order.getEndDate().getMonthValue())
            ));

        // 计算每个月的成本结构
        groupedByMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String month = entry.getKey();
                List<ProductionOrder> monthOrders = entry.getValue();

                BigDecimal materialCost = BigDecimal.ZERO;
                BigDecimal laborCost = BigDecimal.ZERO;
                BigDecimal overheadCost = BigDecimal.ZERO;
                Integer totalQuantity = 0;

                for (ProductionOrder order : monthOrders) {
                    if (order.getMaterialCost() != null) {
                        materialCost = materialCost.add(new BigDecimal(order.getMaterialCost().toString()));
                    }
                    if (order.getLaborCost() != null) {
                        laborCost = laborCost.add(new BigDecimal(order.getLaborCost().toString()));
                    }
                    if (order.getManuCost() != null) {
                        overheadCost = overheadCost.add(new BigDecimal(order.getManuCost().toString()));
                    }
                    if (order.getActualQuantity() != null) {
                        totalQuantity += order.getActualQuantity();
                    }
                }

                BigDecimal totalCost = materialCost.add(laborCost).add(overheadCost);

                if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                    CostStructureVO vo = new CostStructureVO();
                    vo.setYear(Integer.parseInt(month.split("-")[0]));
                    vo.setMonth(month); // 设置月份字段
                    vo.setMaterialCost(materialCost);
                    vo.setLaborCost(laborCost);
                    vo.setOverheadCost(overheadCost);
                    vo.setTotalCost(totalCost);
                    vo.setTotalQuantity(totalQuantity);

                    // 计算单位成本 = 总成本 / 总产量
                    if (totalQuantity > 0) {
                        vo.setUnitCost(totalCost.divide(new BigDecimal(totalQuantity), 2, RoundingMode.HALF_UP));
                    } else {
                        vo.setUnitCost(BigDecimal.ZERO);
                    }

                    // ✅ 计算占比（确保加起来=100%）
                    vo.setMaterialPercent(materialCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
                    vo.setLaborPercent(laborCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
                    vo.setOverheadPercent(overheadCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));

                    result.add(vo);
                }
            });

        return result;
    }

    /**
     * 计算指定年份的成本结构
     */
    private CostStructureVO calculateCostStructure(Integer year, String productLine) {
        BigDecimal materialCost = BigDecimal.ZERO;
        BigDecimal laborCost = BigDecimal.ZERO;
        BigDecimal overheadCost = BigDecimal.ZERO;

        LambdaQueryWrapper<ProductionOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.apply("YEAR(end_date) = {0}", year);
        if (productLine != null && !productLine.isEmpty() && !"all".equals(productLine)) {
            orderWrapper.eq(ProductionOrder::getProductCode, productLine);
        }

        List<ProductionOrder> orders = productionOrderMapper.selectList(orderWrapper);

        for (ProductionOrder order : orders) {
            if (order.getMaterialCost() != null) {
                materialCost = materialCost.add(new BigDecimal(order.getMaterialCost().toString()));
            }
            if (order.getLaborCost() != null) {
                laborCost = laborCost.add(new BigDecimal(order.getLaborCost().toString()));
            }
        }

        LambdaQueryWrapper<ManufacturingCostDetail> costWrapper = new LambdaQueryWrapper<>();
        costWrapper.apply("YEAR(cost_date) = {0}", year);
        if (productLine != null && !productLine.isEmpty() && !"all".equals(productLine)) {
            costWrapper.eq(ManufacturingCostDetail::getProductionLine, productLine);
        }

        List<ManufacturingCostDetail> costs = manufacturingCostDetailMapper.selectList(costWrapper);

        for (ManufacturingCostDetail cost : costs) {
            if (cost.getAmount() != null) {
                overheadCost = overheadCost.add(cost.getAmount());
            }
        }

        BigDecimal totalCost = materialCost.add(laborCost).add(overheadCost);

        if (totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        CostStructureVO vo = new CostStructureVO();
        vo.setYear(year);
        vo.setMaterialCost(materialCost);
        vo.setLaborCost(laborCost);
        vo.setOverheadCost(overheadCost);
        vo.setTotalCost(totalCost);

        vo.setMaterialPercent(materialCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
        vo.setLaborPercent(laborCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
        vo.setOverheadPercent(overheadCost.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));

        return vo;
    }

    /**
     * 获取材料价格趋势
     */
    public List<CostTrendVO> getPriceTrend(String materialCode) {
        List<CostTrendVO> result = new ArrayList<>();

        LambdaQueryWrapper<PurchasePriceHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchasePriceHistory::getMaterialCode, materialCode);
        wrapper.orderByAsc(PurchasePriceHistory::getOrderDate);

        List<PurchasePriceHistory> historyList = purchasePriceHistoryMapper.selectList(wrapper);

        for (PurchasePriceHistory history : historyList) {
            CostTrendVO vo = new CostTrendVO();
            vo.setYear(history.getOrderDate().getYear());
            vo.setMonth(history.getOrderDate().toString());
            vo.setCost(history.getUnitPrice());
            vo.setUnitCost(history.getUnitPrice());
            result.add(vo);
        }

        return result;
    }

    /**
     * 获取最新年份
     */
    private Integer getLatestYear() {
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ProductionOrder::getEndDate);
        wrapper.last("LIMIT 1");

        ProductionOrder latestOrder = productionOrderMapper.selectOne(wrapper);

        if (latestOrder != null && latestOrder.getEndDate() != null) {
            return latestOrder.getEndDate().getYear();
        }

        return null;
    }
}
