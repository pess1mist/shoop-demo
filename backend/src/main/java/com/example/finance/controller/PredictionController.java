package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/prediction")
@Tag(name = "预测管理", description = "收入预测、成本预测等")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;
    
    // 模拟的历史数据（实际应该从数据库查询）
    private static final List<Double> SAMPLE_REVENUE_DATA = Arrays.asList(
        480000.0, 495000.0, 510000.0, 505000.0, 520000.0, 530000.0
    );
    
    private static final List<Double> SAMPLE_COST_DATA = Arrays.asList(
        320000.0, 325000.0, 330000.0, 328000.0, 335000.0, 340000.0
    );
    
    private static final List<Double> SAMPLE_MATERIAL_PRICE_DATA = Arrays.asList(
        2850.0, 2920.0, 2980.0, 3050.0, 3120.0, 3180.0
    );

    @GetMapping("/revenue")
    @Operation(summary = "收入预测", description = "基于历史数据预测未来收入趋势")
    public Result<Map<String, Object>> predictRevenue(
            @RequestParam(defaultValue = "ALL") String deptCode,
            @RequestParam(defaultValue = "6") Integer months,
            @RequestParam(defaultValue = "exponential") String algorithm) {
        
        // 使用指数平滑法预测收入
        List<Double> predictions = predictionService.predictRevenue(
            SAMPLE_REVENUE_DATA, months, 0.25
        );
        
        // 计算置信度
        double confidence = predictionService.calculateConfidence(SAMPLE_REVENUE_DATA, predictions);
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("predictionType", "REVENUE");
        result.put("departmentCode", deptCode);
        result.put("algorithm", "指数平滑法 (Exponential Smoothing)");
        result.put("confidenceLevel", confidence + "%");
        result.put("historical", SAMPLE_REVENUE_DATA);
        result.put("predicted", predictions);
        
        // 生成趋势数据（包含历史和预测）
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        // 添加历史数据
        for (int i = 0; i < SAMPLE_REVENUE_DATA.size(); i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("period", "2025-" + String.format("%02d", i + 1));
            point.put("value", SAMPLE_REVENUE_DATA.get(i));
            point.put("isPredicted", false);
            trendData.add(point);
        }
        
        // 添加预测数据
        for (int i = 0; i < predictions.size(); i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("period", "2026-" + String.format("%02d", i + 1));
            point.put("value", predictions.get(i));
            point.put("isPredicted", true);
            trendData.add(point);
        }
        
        result.put("trendData", trendData);
        
        // 分析结果
        double growthRate = (predictions.get(predictions.size() - 1) - SAMPLE_REVENUE_DATA.get(0)) 
                           / SAMPLE_REVENUE_DATA.get(0) * 100;
        result.put("analysisResult", String.format("预计未来%d个月收入将增长%.2f%%", months, growthRate));
        result.put("suggestion", growthRate > 0 ? 
            "建议加大市场投入，优化产品结构" : "建议控制成本，提升运营效率");
        
        return Result.success(result);
    }

    @GetMapping("/cost")
    @Operation(summary = "成本预测", description = "基于历史数据预测未来成本趋势")
    public Result<Map<String, Object>> predictCost(
            @RequestParam(defaultValue = "ALL") String deptCode,
            @RequestParam(defaultValue = "6") Integer months,
            @RequestParam(defaultValue = "linear") String algorithm) {
        
        // 使用线性回归预测成本
        List<Double> predictions = predictionService.predictTotalCost(SAMPLE_COST_DATA, months);
        
        // 计算置信度
        double confidence = predictionService.calculateConfidence(SAMPLE_COST_DATA, predictions);
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("predictionType", "COST");
        result.put("departmentCode", deptCode);
        result.put("algorithm", "线性回归 (Linear Regression)");
        result.put("confidenceLevel", confidence + "%");
        result.put("historical", SAMPLE_COST_DATA);
        result.put("predicted", predictions);
        
        // 生成趋势数据
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        // 添加历史数据
        for (int i = 0; i < SAMPLE_COST_DATA.size(); i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("period", "2025-" + String.format("%02d", i + 1));
            point.put("value", SAMPLE_COST_DATA.get(i));
            point.put("isPredicted", false);
            trendData.add(point);
        }
        
        // 添加预测数据
        for (int i = 0; i < predictions.size(); i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("period", "2026-" + String.format("%02d", i + 1));
            point.put("value", predictions.get(i));
            point.put("isPredicted", true);
            trendData.add(point);
        }
        
        result.put("trendData", trendData);
        
        // 分析结果
        double changeRate = (predictions.get(predictions.size() - 1) - SAMPLE_COST_DATA.get(0)) 
                           / SAMPLE_COST_DATA.get(0) * 100;
        result.put("analysisResult", String.format("预计未来%d个月成本将变化%.2f%%", months, changeRate));
        result.put("suggestion", changeRate > 5 ? 
            "建议优化供应链，降低采购成本" : "成本控制良好，继续保持");
        
        return Result.success(result);
    }
    
    @GetMapping("/material/price")
    @Operation(summary = "材料价格预测", description = "基于移动平均法预测材料价格")
    public Result<Map<String, Object>> predictMaterialPrice(
            @RequestParam String materialCode,
            @RequestParam(defaultValue = "3") Integer months,
            @RequestParam(defaultValue = "3") Integer window) {
        
        // 使用移动平均法预测材料价格
        List<Double> predictions = predictionService.predictMaterialPrice(
            SAMPLE_MATERIAL_PRICE_DATA, months, window
        );
        
        // 计算置信度
        double confidence = predictionService.calculateConfidence(SAMPLE_MATERIAL_PRICE_DATA, predictions);
        
        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("materialCode", materialCode);
        result.put("algorithm", "移动平均法 (Moving Average)");
        result.put("confidenceLevel", confidence + "%");
        result.put("historical", SAMPLE_MATERIAL_PRICE_DATA);
        result.put("predicted", predictions);
        
        // 生成趋势数据
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        // 添加历史数据
        for (int i = 0; i < SAMPLE_MATERIAL_PRICE_DATA.size(); i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("period", "2025-" + String.format("%02d", i + 1));
            point.put("value", SAMPLE_MATERIAL_PRICE_DATA.get(i));
            point.put("isPredicted", false);
            trendData.add(point);
        }
        
        // 添加预测数据
        for (int i = 0; i < predictions.size(); i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("period", "2026-" + String.format("%02d", i + 1));
            point.put("value", predictions.get(i));
            point.put("isPredicted", true);
            trendData.add(point);
        }
        
        result.put("trendData", trendData);
        
        // 分析结果
        double avgPrice = predictions.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        result.put("analysisResult", String.format("预计未来%d个月平均价格为%.2f元", months, avgPrice));
        result.put("suggestion", avgPrice > 3000 ? 
            "建议提前采购，锁定价格" : "建议按需采购，降低库存");
        
        return Result.success(result);
    }
    
    @GetMapping("/algorithm/config")
    @Operation(summary = "获取算法配置", description = "获取所有可用的预测算法及其说明")
    public Result<List<Map<String, String>>> getAlgorithmConfig() {
        List<Map<String, String>> algorithms = new ArrayList<>();
        
        Map<String, String> ma = new HashMap<>();
        ma.put("code", "moving_average");
        ma.put("name", "移动平均法");
        ma.put("description", "适合短期价格预测，窗口期通常取 3 个月");
        ma.put("scenario", "价格波动平稳的场景");
        algorithms.add(ma);
        
        Map<String, String> lr = new HashMap<>();
        lr.put("code", "linear_regression");
        lr.put("name", "线性回归");
        lr.put("description", "适合成本趋势预测");
        lr.put("scenario", "确保数据无明显季节性");
        algorithms.add(lr);
        
        Map<String, String> es = new HashMap<>();
        es.put("code", "exponential_smoothing");
        es.put("name", "指数平滑法");
        es.put("description", "平滑系数α通常取 0.2~0.3");
        es.put("scenario", "收入预测，考虑近期数据权重");
        algorithms.add(es);
        
        return Result.success(algorithms);
    }
}
