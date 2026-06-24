package com.example.finance.service;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 预测服务 - 实现多种预测算法
 */
@Service
public class PredictionService {

    /**
     * 1. 移动平均法（适合短期价格预测）
     * @param data 历史数据
     * @param window 窗口大小（月数）
     * @return 预测值
     */
    public double movingAverage(List<Double> data, int window) {
        if (data.size() < window) {
            return data.isEmpty() ? 0 : data.get(data.size() - 1);
        }
        
        double sum = 0;
        for (int i = data.size() - window; i < data.size(); i++) {
            sum += data.get(i);
        }
        
        return sum / window;
    }

    /**
     * 2. 线性回归（适合成本趋势预测）
     * @param x 自变量（时间索引）
     * @param y 因变量（实际值）
     * @return [斜率，截距]
     */
    public double[] linearRegression(List<Integer> x, List<Double> y) {
        int n = x.size();
        if (n == 0) {
            return new double[]{0, 0};
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXY += x.get(i) * y.get(i);
            sumX2 += x.get(i) * x.get(i);
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        return new double[]{slope, intercept};
    }

    /**
     * 3. 指数平滑法
     * @param data 历史数据
     * @param alpha 平滑系数（0.2~0.3）
     * @return 预测值
     */
    public double exponentialSmoothing(List<Double> data, double alpha) {
        if (data.isEmpty()) {
            return 0;
        }
        
        double forecast = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            forecast = alpha * data.get(i) + (1 - alpha) * forecast;
        }
        
        return forecast;
    }

    /**
     * 预测材料价格（使用移动平均法）
     * @param historicalData 历史价格数据
     * @param months 预测月数
     * @param window 移动平均窗口
     * @return 预测结果列表
     */
    public List<Double> predictMaterialPrice(List<Double> historicalData, int months, int window) {
        List<Double> predictions = new ArrayList<>();
        
        // 使用移动平均法预测未来几个月
        double baseValue = movingAverage(historicalData, window);
        
        for (int i = 0; i < months; i++) {
            // 添加小幅随机波动（模拟市场变化）
            double fluctuation = (Math.random() - 0.5) * 0.05 * baseValue;
            predictions.add(baseValue + fluctuation);
        }
        
        return predictions;
    }

    /**
     * 预测总成本（使用线性回归）
     * @param historicalData 历史成本数据
     * @param periods 预测期数
     * @return 预测结果列表
     */
    public List<Double> predictTotalCost(List<Double> historicalData, int periods) {
        List<Double> predictions = new ArrayList<>();
        
        if (historicalData.isEmpty()) {
            for (int i = 0; i < periods; i++) {
                predictions.add(0.0);
            }
            return predictions;
        }

        // 准备数据
        List<Integer> x = new ArrayList<>();
        List<Double> y = historicalData;
        
        for (int i = 0; i < y.size(); i++) {
            x.add(i + 1);
        }

        // 线性回归
        double[] params = linearRegression(x, y);
        double slope = params[0];
        double intercept = params[1];

        // 预测未来
        int lastPeriod = x.size();
        for (int i = 1; i <= periods; i++) {
            double predicted = slope * (lastPeriod + i) + intercept;
            predictions.add(Math.max(0, predicted)); // 成本不能为负
        }

        return predictions;
    }

    /**
     * 预测收入（使用指数平滑）
     * @param historicalData 历史收入数据
     * @param months 预测月数
     * @param alpha 平滑系数
     * @return 预测结果列表
     */
    public List<Double> predictRevenue(List<Double> historicalData, int months, double alpha) {
        List<Double> predictions = new ArrayList<>();
        
        if (historicalData.isEmpty()) {
            for (int i = 0; i < months; i++) {
                predictions.add(0.0);
            }
            return predictions;
        }

        double baseValue = exponentialSmoothing(historicalData, alpha);
        
        for (int i = 0; i < months; i++) {
            // 考虑增长趋势
            double growthRate = 0.02; // 假设月增长率 2%
            double predicted = baseValue * Math.pow(1 + growthRate, i + 1);
            predictions.add(predicted);
        }
        
        return predictions;
    }

    /**
     * 计算预测置信度
     * @param historicalData 历史数据
     * @param predictedData 预测数据
     * @return 置信度百分比
     */
    public double calculateConfidence(List<Double> historicalData, List<Double> predictedData) {
        if (historicalData.isEmpty() || predictedData.isEmpty()) {
            return 0;
        }

        // 简单计算：基于历史数据波动性
        double avg = historicalData.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = 0;
        
        for (Double value : historicalData) {
            variance += Math.pow(value - avg, 2);
        }
        variance /= historicalData.size();
        
        double stdDev = Math.sqrt(variance);
        double cv = stdDev / avg; // 变异系数
        
        // 变异系数越小，置信度越高
        double confidence = Math.max(0, Math.min(100, (1 - cv) * 100));
        
        return Math.round(confidence * 10) / 10.0;
    }
}
