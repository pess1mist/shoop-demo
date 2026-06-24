package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.ManufacturingCostDetail;
import com.example.finance.mapper.ManufacturingCostDetailMapper;
import com.example.finance.service.ManufacturingCostDetailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 制造费用明细服务实现类
 */
@Service
public class ManufacturingCostDetailServiceImpl extends ServiceImpl<ManufacturingCostDetailMapper, ManufacturingCostDetail> implements ManufacturingCostDetailService {

    @Override
    public List<ManufacturingCostDetail> listAll() {
        return this.list();
    }

    @Override
    public List<ManufacturingCostDetail> listByProductionLine(String productionLine) {
        LambdaQueryWrapper<ManufacturingCostDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ManufacturingCostDetail::getProductionLine, productionLine);
        return this.list(wrapper);
    }

    @Override
    public List<ManufacturingCostDetail> listByCostCategory(String costCategory) {
        LambdaQueryWrapper<ManufacturingCostDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ManufacturingCostDetail::getCostCategory, costCategory);
        return this.list(wrapper);
    }

    @Override
    public String calculateTotalCost(String productionLine) {
        List<ManufacturingCostDetail> costList = listByProductionLine(productionLine);
        if (costList.isEmpty()) {
            return "暂无费用数据";
        }

        BigDecimal totalCost = costList.stream()
                .map(ManufacturingCostDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return String.format("生产线%s：总费用%.2f 元，共%d 笔费用",
                productionLine, totalCost, costList.size());
    }
}
