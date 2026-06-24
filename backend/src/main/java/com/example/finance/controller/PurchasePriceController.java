package com.example.finance.controller;

import com.example.finance.dto.Result;
import com.example.finance.entity.PurchasePriceHistory;
import com.example.finance.service.PurchasePriceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购价格历史控制器
 */
@RestController
@RequestMapping("/api/purchase-price")
@CrossOrigin(origins = "*")
public class PurchasePriceController {

    @Autowired
    private PurchasePriceHistoryService purchasePriceService;

    /**
     * 查询某材料的价格历史
     */
    @GetMapping("/list/{materialCode}")
    public Result<List<PurchasePriceHistory>> listByMaterialCode(@PathVariable String materialCode) {
        List<PurchasePriceHistory> result = purchasePriceService.listByMaterialCode(materialCode);
        return Result.success(result);
    }

    /**
     * 分析价格走势
     */
    @GetMapping("/analyze/{materialCode}")
    public Result<String> analyzePriceTrend(@PathVariable String materialCode) {
        String result = purchasePriceService.analyzePriceTrend(materialCode);
        return Result.success(result);
    }
}
