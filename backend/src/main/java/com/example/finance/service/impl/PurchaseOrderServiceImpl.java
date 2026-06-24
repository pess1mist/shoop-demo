package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.PurchaseOrder;
import com.example.finance.mapper.PurchaseOrderMapper;
import com.example.finance.service.PurchaseOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 采购订单服务实现类
 */
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Override
    public List<PurchaseOrder> listAll() {
        return this.list();
    }

    @Override
    public List<PurchaseOrder> listBySupplier(String supplierId) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrder::getSupplierId, supplierId);
        return this.list(wrapper);
    }

    @Override
    public List<PurchaseOrder> listByMaterial(String materialCode) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrder::getMaterialCode, materialCode);
        return this.list(wrapper);
    }
}
