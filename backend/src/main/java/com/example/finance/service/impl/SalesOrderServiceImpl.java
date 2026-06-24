package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.SalesOrder;
import com.example.finance.mapper.SalesOrderMapper;
import com.example.finance.service.SalesOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 销售订单服务实现类
 */
@Service
public class SalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder> implements SalesOrderService {

    @Override
    public List<SalesOrder> listAll() {
        return list();
    }

    @Override
    public List<SalesOrder> listByCustomer(String customerId) {
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesOrder::getCustomerId, customerId);
        return list(wrapper);
    }

    @Override
    public List<SalesOrder> listByProduct(String productCode) {
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesOrder::getProductCode, productCode);
        return list(wrapper);
    }
}
