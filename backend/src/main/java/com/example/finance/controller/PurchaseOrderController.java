package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.PurchaseOrder;
import com.example.finance.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购订单控制器
 */
@Tag(name = "采购订单管理", description = "提供采购订单的增删改查功能")
@RestController
@RequestMapping("/api/purchase-order")
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 查询所有采购订单
     */
    @Operation(summary = "查询所有采购订单", description = "返回系统中所有的采购订单列表")
    @GetMapping("/list")
    public Result<List<PurchaseOrder>> listAll() {
        List<PurchaseOrder> result = purchaseOrderService.listAll();
        return Result.success(result);
    }

    /**
     * 根据供应商查询订单
     */
    @Operation(summary = "根据供应商查询订单", description = "根据供应商 ID 查询其所有采购订单")
    @GetMapping("/list/{supplierId}")
    public Result<List<PurchaseOrder>> listBySupplier(
            @Parameter(description = "供应商 ID", example = "S01") 
            @PathVariable String supplierId) {
        List<PurchaseOrder> result = purchaseOrderService.listBySupplier(supplierId);
        return Result.success(result);
    }

    /**
     * 根据材料查询订单
     */
    @Operation(summary = "根据材料查询订单", description = "根据材料编码查询其所有采购订单")
    @GetMapping("/list/material/{materialCode}")
    public Result<List<PurchaseOrder>> listByMaterial(
            @Parameter(description = "材料编码", example = "M1") 
            @PathVariable String materialCode) {
        List<PurchaseOrder> result = purchaseOrderService.listByMaterial(materialCode);
        return Result.success(result);
    }

    /**
     * 新增采购订单
     */
    @Operation(summary = "新增采购订单", description = "创建新的采购订单，需要校验字段完整性")
    @PostMapping("/add")
    public Result<Boolean> add(
            @Parameter(description = "采购订单对象", required = true) 
            @RequestBody @Validated PurchaseOrder purchaseOrder) {
        boolean result = purchaseOrderService.save(purchaseOrder);
        return result ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    /**
     * 删除采购订单
     */
    @Operation(summary = "删除采购订单", description = "根据订单 ID 删除指定的采购订单")
    @DeleteMapping("/{orderId}")
    public Result<Boolean> delete(
            @Parameter(description = "订单 ID", example = "PO202603240001", required = true) 
            @PathVariable String orderId) {
        boolean result = purchaseOrderService.removeById(orderId);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    /**
     * 更新采购订单
     */
    @Operation(summary = "更新采购订单", description = "更新已存在的采购订单信息")
    @PutMapping("/update")
    public Result<Boolean> update(
            @Parameter(description = "采购订单对象", required = true) 
            @RequestBody @Validated PurchaseOrder purchaseOrder) {
        boolean result = purchaseOrderService.updateById(purchaseOrder);
        return result ? Result.success("更新成功", true) : Result.error("更新失败");
    }
}
