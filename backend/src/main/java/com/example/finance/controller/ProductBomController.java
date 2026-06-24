package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.ProductBom;
import com.example.finance.service.ProductBomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品 BOM 控制器
 */
@RestController
@RequestMapping("/api/product-bom")
@CrossOrigin(origins = "*")
public class ProductBomController {

    @Autowired
    private ProductBomService productBomService;

    /**
     * 查询某产品的 BOM 清单
     */
    @GetMapping("/list/{productCode}")
    public Result<List<ProductBom>> listByProductCode(@PathVariable String productCode) {
        List<ProductBom> result = productBomService.listByProductCode(productCode);
        return Result.success(result);
    }

    /**
     * 查询某材料用于哪些产品
     */
    @GetMapping("/list/material/{materialCode}")
    public Result<List<ProductBom>> listByMaterialCode(@PathVariable String materialCode) {
        List<ProductBom> result = productBomService.listByMaterialCode(materialCode);
        return Result.success(result);
    }
}
