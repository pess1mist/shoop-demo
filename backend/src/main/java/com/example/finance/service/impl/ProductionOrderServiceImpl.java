package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.ProductionOrder;
import com.example.finance.mapper.ProductionOrderMapper;
import com.example.finance.service.ProductionOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 生产订单服务实现类
 */
@Service
public class ProductionOrderServiceImpl extends ServiceImpl<ProductionOrderMapper, ProductionOrder> implements ProductionOrderService {

    @Override
    public List<ProductionOrder> listAll() {
        return this.list();
    }

    @Override
    public List<ProductionOrder> listByProductCode(String productCode) {
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionOrder::getProductCode, productCode);
        return this.list(wrapper);
    }

    @Override
    public String calculateCostSummary(String productCode) {
        List<ProductionOrder> orderList = listByProductCode(productCode);
        if (orderList.isEmpty()) {
            return "暂无生产数据";
        }

        int totalMaterialCost = orderList.stream()
                .mapToInt(ProductionOrder::getMaterialCost)
                .sum();

        int totalLaborCost = orderList.stream()
                .mapToInt(ProductionOrder::getLaborCost)
                .sum();

        int totalManuCost = orderList.stream()
                .mapToInt(ProductionOrder::getManuCost)
                .sum();

        int totalCost = orderList.stream()
                .mapToInt(ProductionOrder::getTotalCost)
                .sum();

        return String.format("产品%s：材料成本%.2f 元，人工成本%.2f 元，制造费用%.2f 元，总成本%.2f 元",
                productCode, (double)totalMaterialCost, (double)totalLaborCost, (double)totalManuCost, (double)totalCost);
    }

    @Override
    public List<ProductionOrder> listByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        
        // 动态添加查询条件
        if (params != null) {
            // 产品编码条件
            String productCode = (String) params.get("productCode");
            if (StringUtils.hasText(productCode) && !"all".equals(productCode)) {
                wrapper.eq(ProductionOrder::getProductCode, productCode);
            }
        }
        
        // 按开始日期降序排列
        wrapper.orderByDesc(ProductionOrder::getStartDate);
        
        return this.list(wrapper);
    }
}
