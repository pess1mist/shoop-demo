package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.*;
import com.example.finance.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 毕业设计数据管理控制器
 * 统一管理智能制造与内控系统的所有业务数据
 */
@Tag(name = "毕业设计数据管理", description = "提供智能制造内控管理系统的完整数据查询功能")
@RestController
@RequestMapping("/api/graduation-data")
@CrossOrigin(origins = "*")
public class GraduationDataController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private BudgetExecutionService budgetExecutionService;

    @Autowired
    private ManufacturingCostDetailService manufacturingCostDetailService;

    @Autowired
    private ProductBomService productBomService;

    @Autowired
    private ProductionOrderService productionOrderService;

    @Autowired
    private InternalControlLogService internalControlLogService;

    @Autowired
    private BusinessDataService businessDataService;

    @Autowired
    private FinancialDataService financialDataService;

    /**
     * 获取所有业务数据概览
     */
    @Operation(summary = "获取所有业务数据概览", description = "返回各业务模块的数据数量统计")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // 统计各模块数据数量
            overview.put("purchaseOrders", purchaseOrderService.listAll().size());
            overview.put("budgetExecutions", budgetExecutionService.listAll().size());
            overview.put("manufacturingCosts", manufacturingCostDetailService.listAll().size());
            overview.put("productBoms", productBomService.listAll().size());
            overview.put("productionOrders", productionOrderService.listAll().size());
            overview.put("internalControlLogs", internalControlLogService.listAll().size());
            overview.put("businessData", businessDataService.listAll().size());
            overview.put("financialData", financialDataService.listAll().size());
            
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("获取概览数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取采购订单数据
     */
    @Operation(summary = "获取采购订单数据", description = "返回所有采购订单列表")
    @GetMapping("/purchase-orders")
    public Result<List<PurchaseOrder>> getPurchaseOrders() {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.listAll();
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error("获取采购订单失败：" + e.getMessage());
        }
    }

    /**
     * 获取预算执行数据
     */
    @Operation(summary = "获取预算执行数据", description = "返回所有预算执行对比数据")
    @GetMapping("/budget-executions")
    public Result<List<BudgetExecution>> getBudgetExecutions() {
        try {
            List<BudgetExecution> executions = budgetExecutionService.listAll();
            return Result.success(executions);
        } catch (Exception e) {
            return Result.error("获取预算执行数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取制造成本明细
     */
    @Operation(summary = "获取制造成本明细", description = "返回所有产品制造成本明细数据")
    @GetMapping("/manufacturing-costs")
    public Result<List<ManufacturingCostDetail>> getManufacturingCosts() {
        try {
            List<ManufacturingCostDetail> costs = manufacturingCostDetailService.listAll();
            return Result.success(costs);
        } catch (Exception e) {
            return Result.error("获取制造成本明细失败：" + e.getMessage());
        }
    }

    /**
     * 获取产品 BOM 数据
     */
    @Operation(summary = "获取产品 BOM 数据", description = "返回所有产品的物料清单数据")
    @GetMapping("/product-boms")
    public Result<List<ProductBom>> getProductBoms() {
        try {
            List<ProductBom> boms = productBomService.listAll();
            return Result.success(boms);
        } catch (Exception e) {
            return Result.error("获取产品 BOM 数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取生产订单数据
     */
    @Operation(summary = "获取生产订单数据", description = "返回所有生产订单列表")
    @GetMapping("/production-orders")
    public Result<List<ProductionOrder>> getProductionOrders() {
        try {
            List<ProductionOrder> orders = productionOrderService.listAll();
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error("获取生产订单数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取内控预警日志
     */
    @Operation(summary = "获取内控预警日志", description = "返回所有内控预警记录")
    @GetMapping("/internal-control-logs")
    public Result<List<InternalControlLog>> getInternalControlLogs() {
        try {
            List<InternalControlLog> logs = internalControlLogService.listAll();
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("获取内控预警日志失败：" + e.getMessage());
        }
    }

    /**
     * 获取业务经营数据
     */
    @Operation(summary = "获取业务经营数据", description = "返回所有业务经营指标数据")
    @GetMapping("/business-data")
    public Result<List<BusinessData>> getBusinessData() {
        try {
            List<BusinessData> data = businessDataService.listAll();
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取业务经营数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取财务报表数据
     */
    @Operation(summary = "获取财务报表数据", description = "返回所有财务报表数据")
    @GetMapping("/financial-data")
    public Result<List<FinancialData>> getFinancialData() {
        try {
            List<FinancialData> data = financialDataService.listAll();
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取财务报表数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取完整数据包（所有数据）
     */
    @Operation(summary = "获取完整数据包", description = "返回所有业务模块的完整数据，用于数据迁移或备份")
    @GetMapping("/full-package")
    public Result<Map<String, Object>> getFullPackage() {
        Map<String, Object> fullData = new HashMap<>();
        
        try {
            fullData.put("purchaseOrders", purchaseOrderService.listAll());
            fullData.put("budgetExecutions", budgetExecutionService.listAll());
            fullData.put("manufacturingCosts", manufacturingCostDetailService.listAll());
            fullData.put("productBoms", productBomService.listAll());
            fullData.put("productionOrders", productionOrderService.listAll());
            fullData.put("internalControlLogs", internalControlLogService.listAll());
            fullData.put("businessData", businessDataService.listAll());
            fullData.put("financialData", financialDataService.listAll());
            
            return Result.success(fullData);
        } catch (Exception e) {
            return Result.error("获取完整数据包失败：" + e.getMessage());
        }
    }
}
