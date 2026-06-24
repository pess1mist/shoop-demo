package com.example.finance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.finance.common.BaseController;
import com.example.finance.dto.Result;
import com.example.finance.entity.Product;
import com.example.finance.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品管理 Controller
 */
@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*")
public class ProductController extends BaseController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/list")
    public Result<List<Product>> list() {
        return executeQuery(() -> productService.listAll(), "查询产品失败");
    }
    
    @GetMapping("/page")
    public Result<Page<Product>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return executeQuery(() -> {
            Page<Product> page = new Page<>(pageNum, pageSize);
            return productService.page(page);
        }, "分页查询产品失败");
    }
    
    @GetMapping("/{productCode}")
    public Result<Product> getByProductCode(@PathVariable String productCode) {
        return executeQuery(() -> {
            Product product = productService.getByProductCode(productCode);
            if (product == null) {
                throw new RuntimeException("产品不存在");
            }
            return product;
        }, "查询产品失败");
    }
    
    @GetMapping("/category/{category}")
    public Result<List<Product>> listByCategory(@PathVariable String category) {
        return executeQuery(() -> productService.listByCategory(category), "按类别查询产品失败");
    }
    
    @PostMapping
    public Result<Boolean> save(@RequestBody Product product) {
        return executeSave(() -> productService.save(product), "新增产品失败");
    }
    
    @PutMapping
    public Result<Boolean> update(@RequestBody Product product) {
        return executeUpdate(() -> productService.updateById(product), "更新产品失败");
    }
    
    @DeleteMapping("/{productCode}")
    public Result<Boolean> delete(@PathVariable String productCode) {
        return executeDelete(() -> {
            Product product = productService.getByProductCode(productCode);
            if (product == null) {
                throw new RuntimeException("产品不存在");
            }
            return productService.removeById(product.getProductCode());
        }, "删除产品失败");
    }
}
