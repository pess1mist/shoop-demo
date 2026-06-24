package com.example.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.entity.MaterialPriceFluctuation;
import com.example.finance.mapper.MaterialPriceFluctuationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 材料采购价格波动 Controller
 */
@RestController
@RequestMapping("/api/material/price")
@CrossOrigin(origins = "*")
public class MaterialPriceController {
    
    @Autowired
    private MaterialPriceFluctuationMapper priceMapper;
    
    /**
     * 分页查询列表
     */
    @GetMapping("/list")
    public Map<String, Object> getList(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(required = false) String materialCode
    ) {
        Page<MaterialPriceFluctuation> mpPage = new Page<>(page, size);
        QueryWrapper<MaterialPriceFluctuation> wrapper = new QueryWrapper<>();
        
        if (materialCode != null && !materialCode.isEmpty()) {
            wrapper.eq("material_code", materialCode);
        }
        wrapper.orderByDesc("purchase_date");
        
        Page<MaterialPriceFluctuation> result = priceMapper.selectPage(mpPage, wrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("records", result.getRecords());
        response.put("total", result.getTotal());
        response.put("size", result.getSize());
        response.put("current", result.getCurrent());
        
        return response;
    }
    
    /**
     * 获取价格波动趋势
     */
    @GetMapping("/fluctuation/analysis")
    public Map<String, Object> getFluctuationAnalysis(
        @RequestParam String materialCode,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询价格趋势
        List<Map<String, Object>> trend = priceMapper.getPriceTrend(
            materialCode,
            startDate.toString().substring(0, 10),
            endDate.toString().substring(0, 10)
        );
        
        // 计算统计数据
        if (!trend.isEmpty()) {
            BigDecimal avgPrice = trend.stream()
                .map(m -> (BigDecimal) m.get("unit_price"))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(trend.size()), 2, BigDecimal.ROUND_HALF_UP);
            
            BigDecimal maxPrice = trend.stream()
                .map(m -> (BigDecimal) m.get("unit_price"))
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            BigDecimal minPrice = trend.stream()
                .map(m -> (BigDecimal) m.get("unit_price"))
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            result.put("trend", trend);
            result.put("avgPrice", avgPrice);
            result.put("maxPrice", maxPrice);
            result.put("minPrice", minPrice);
            result.put("fluctuationRange", maxPrice.subtract(minPrice));
        } else {
            result.put("trend", new ArrayList<>());
            result.put("avgPrice", BigDecimal.ZERO);
            result.put("maxPrice", BigDecimal.ZERO);
            result.put("minPrice", BigDecimal.ZERO);
            result.put("fluctuationRange", BigDecimal.ZERO);
        }
        
        return result;
    }
    
    /**
     * 获取预算差异分析
     */
    @GetMapping("/budget/variance")
    public Map<String, Object> getBudgetVariance(
        @RequestParam String startPeriod,
        @RequestParam String endPeriod
    ) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> varianceList = priceMapper.getBudgetVariance(startPeriod, endPeriod);
        
        BigDecimal totalBudget = varianceList.stream()
            .map(m -> (BigDecimal) m.get("totalBudget"))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalActual = varianceList.stream()
            .map(m -> (BigDecimal) m.get("totalActual"))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalVariance = varianceList.stream()
            .map(m -> (BigDecimal) m.get("totalVariance"))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        result.put("varianceList", varianceList);
        result.put("totalBudget", totalBudget);
        result.put("totalActual", totalActual);
        result.put("totalVariance", totalVariance);
        result.put("varianceRate", totalBudget.compareTo(BigDecimal.ZERO) > 0 ? 
            totalVariance.divide(totalBudget, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) : 
            BigDecimal.ZERO);
        
        return result;
    }
    
    /**
     * 获取价格波动统计
     */
    @GetMapping("/fluctuation/statistics")
    public Map<String, Object> getFluctuationStatistics(
        @RequestParam String period
    ) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> statistics = priceMapper.getFluctuationStatistics(period);
        
        result.put("statistics", statistics);
        result.put("count", statistics.size());
        
        return result;
    }
    
    /**
     * 添加价格波动记录
     */
    @PostMapping("/add")
    public Map<String, Object> addPriceRecord(@RequestBody MaterialPriceFluctuation record) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            priceMapper.insert(record);
            result.put("success", true);
            result.put("message", "添加成功");
            result.put("id", record.getId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量导入数据
     */
    @PostMapping("/batch/import")
    public Map<String, Object> batchImport(@RequestBody List<MaterialPriceFluctuation> records) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int successCount = 0;
            for (MaterialPriceFluctuation record : records) {
                priceMapper.insert(record);
                successCount++;
            }
            
            result.put("success", true);
            result.put("message", "批量导入成功");
            result.put("count", successCount);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量导入失败：" + e.getMessage());
        }
        
        return result;
    }
}
