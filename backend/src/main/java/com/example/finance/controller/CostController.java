package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.service.CostService;
import com.example.finance.service.ProductService;
import com.example.finance.vo.CostStructureVO;
import com.example.finance.vo.CostTrendVO;
import com.example.finance.entity.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成本管理控制器
 */
@Tag(name = "成本管理", description = "成本趋势分析和成本结构分析接口")
@RestController
@RequestMapping("/api/cost")
@CrossOrigin(origins = "*")
public class CostController {

    @Autowired
    private CostService costService;

    @Autowired
    private ProductService productService;

    /**
     * 获取成本趋势
     * GET /api/cost/trend?year=2025&productCode=P01
     * 注：如果不传时间参数，返回所有历史数据（实时累计）
     */
    @GetMapping("/trend")
    @Operation(summary = "获取成本趋势", description = "返回成本趋势数据，不传时间参数则返回所有历史数据")
    public Result<List<CostTrendVO>> getCostTrend(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String productCode) {
        
        List<CostTrendVO> data = costService.getCostTrend(year, startDate, endDate, productCode);
        return Result.success(data);
    }

    /**
     * 获取成本结构（按年份分组）
     * GET /api/cost/structure?yearRange=2023,2025&product=P01
     * 注：如果不传 yearRange，默认返回 2023 年至今的所有数据（实时累计）
     */
    @GetMapping("/structure")
    @Operation(summary = "获取成本结构", description = "返回成本结构数据，不传 yearRange 则返回 2023 年至今数据")
    public Result<List<CostStructureVO>> getCostStructure(
            @RequestParam(required = false) String yearRange,
            @RequestParam(required = false) String product) {
        
        List<CostStructureVO> data = costService.getCostStructure(yearRange, product);
        return Result.success(data);
    }

    /**
     * 获取产品列表
     * GET /api/cost/products
     */
    @GetMapping("/products")
    @Operation(summary = "获取产品列表")
    public Result<List<Product>> getProductList() {
        List<Product> products = productService.listAll();
        return Result.success(products);
    }

    /**
     * 获取材料价格趋势
     */
    @GetMapping("/priceTrend")
    @Operation(summary = "获取材料价格趋势")
    public Result<List<CostTrendVO>> getPriceTrend(@RequestParam String materialCode) {
        List<CostTrendVO> data = costService.getPriceTrend(materialCode);
        return Result.success(data);
    }
}
