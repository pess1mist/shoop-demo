package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.PurchasePriceHistory;
import com.example.finance.mapper.PurchasePriceHistoryMapper;
import com.example.finance.service.PurchasePriceHistoryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购价格历史服务实现类
 */
@Service
public class PurchasePriceHistoryServiceImpl extends ServiceImpl<PurchasePriceHistoryMapper, PurchasePriceHistory> implements PurchasePriceHistoryService {

    @Override
    public List<PurchasePriceHistory> listByMaterialCode(String materialCode) {
        LambdaQueryWrapper<PurchasePriceHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchasePriceHistory::getMaterialCode, materialCode)
               .orderByAsc(PurchasePriceHistory::getOrderDate);
        return this.list(wrapper);
    }

    @Override
    public String analyzePriceTrend(String materialCode) {
        List<PurchasePriceHistory> historyList = listByMaterialCode(materialCode);
        if (historyList.isEmpty()) {
            return "暂无价格数据";
        }

        BigDecimal minPrice = historyList.stream()
                .map(PurchasePriceHistory::getUnitPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = historyList.stream()
                .map(PurchasePriceHistory::getUnitPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal avgPrice = historyList.stream()
                .map(PurchasePriceHistory::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(historyList.size()), 2, BigDecimal.ROUND_HALF_UP);

        return String.format("材料%s：平均价格%.2f 元，最高%.2f 元，最低%.2f 元，波动幅度%.2f%%",
                materialCode, avgPrice, maxPrice, minPrice,
                maxPrice.subtract(minPrice).divide(minPrice, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)));
    }
}
