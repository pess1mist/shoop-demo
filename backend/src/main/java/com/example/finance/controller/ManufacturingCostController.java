package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.ManufacturingCostDetail;
import com.example.finance.service.ManufacturingCostDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 制造费用明细控制器
 */
@RestController
@RequestMapping("/api/manufacturing-cost")
@CrossOrigin(origins = "*")
public class ManufacturingCostController {

    @Autowired
    private ManufacturingCostDetailService manufacturingCostService;

    /**
     * 按生产线查询费用
     */
    @GetMapping("/list/{productionLine}")
    public Result<List<ManufacturingCostDetail>> listByProductionLine(@PathVariable String productionLine) {
        List<ManufacturingCostDetail> result = manufacturingCostService.listByProductionLine(productionLine);
        return Result.success(result);
    }

    /**
     * 按费用类别查询
     */
    @GetMapping("/list/category/{costCategory}")
    public Result<List<ManufacturingCostDetail>> listByCostCategory(@PathVariable String costCategory) {
        List<ManufacturingCostDetail> result = manufacturingCostService.listByCostCategory(costCategory);
        return Result.success(result);
    }

    /**
     * 统计某生产线总费用
     */
    @GetMapping("/total/{productionLine}")
    public Result<String> calculateTotalCost(@PathVariable String productionLine) {
        String result = manufacturingCostService.calculateTotalCost(productionLine);
        return Result.success(result);
    }
}
