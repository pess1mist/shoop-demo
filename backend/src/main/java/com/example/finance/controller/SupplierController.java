package com.example.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.common.BaseController;
import com.example.finance.dto.Result;
import com.example.finance.entity.Supplier;
import com.example.finance.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商管理 Controller
 */
@RestController
@RequestMapping("/api/supplier")
@CrossOrigin(origins = "*")
public class SupplierController extends BaseController {
    
    @Autowired
    private SupplierService supplierService;
    
    @GetMapping("/list")
    public Result<List<Supplier>> list() {
        return executeQuery(() -> supplierService.listAll(), "查询供应商失败");
    }
    
    @GetMapping("/page")
    public Result<Page<Supplier>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return executeQuery(() -> {
            Page<Supplier> page = new Page<>(pageNum, pageSize);
            return supplierService.page(page);
        }, "分页查询供应商失败");
    }
    
    @GetMapping("/{supplierCode}")
    public Result<Supplier> getBySupplierCode(@PathVariable String supplierCode) {
        return executeQuery(() -> {
            Supplier supplier = supplierService.getBySupplierCode(supplierCode);
            if (supplier == null) {
                throw new RuntimeException("供应商不存在");
            }
            return supplier;
        }, "查询供应商失败");
    }
    
    @GetMapping("/rating/{rating}")
    public Result<List<Supplier>> listByRating(@PathVariable String rating) {
        return executeQuery(() -> supplierService.listByRating(rating), "按评级查询供应商失败");
    }
    
    @PostMapping
    public Result<Boolean> save(@RequestBody Supplier supplier) {
        return executeSave(() -> supplierService.save(supplier), "新增供应商失败");
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody Supplier supplier) {
        return executeUpdate(() -> supplierService.updateById(supplier), "更新供应商失败");
    }
    
    @DeleteMapping("/{supplierCode}")
    public Result<Boolean> delete(@PathVariable String supplierCode) {
        return executeDelete(() -> {
            Supplier supplier = supplierService.getBySupplierCode(supplierCode);
            if (supplier == null) {
                throw new RuntimeException("供应商不存在");
            }
            return supplierService.removeById(supplier.getId());
        }, "删除供应商失败");
    }
}
