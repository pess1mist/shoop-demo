package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.ProductionOrder;
import com.example.finance.service.ProductionOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生产订单控制器
 */
@RestController
@RequestMapping("/api/production-order")
@CrossOrigin(origins = "*")
public class ProductionOrderController {

    @Autowired
    private ProductionOrderService productionOrderService;

    /**
     * 查询所有生产订单
     */
    @GetMapping("/list")
    public Result<List<ProductionOrder>> listAll() {
        List<ProductionOrder> result = productionOrderService.listAll();
        return Result.success(result);
    }

    /**
     * 查询某产品的生产订单
     */
    @GetMapping("/list/{productCode}")
    public Result<List<ProductionOrder>> listByProductCode(@PathVariable String productCode) {
        List<ProductionOrder> result = productionOrderService.listByProductCode(productCode);
        return Result.success(result);
    }

    /**
     * 统计某产品的成本
     */
    @GetMapping("/cost-summary/{productCode}")
    public Result<String> calculateCostSummary(@PathVariable String productCode) {
        String result = productionOrderService.calculateCostSummary(productCode);
        return Result.success(result);
    }
}
