package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.Supplier;
import com.example.finance.mapper.SupplierMapper;
import com.example.finance.service.SupplierService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 供应商 Service 实现类
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {
    
    @Override
    public List<Supplier> listAll() {
        return this.list();
    }
    
    @Override
    public Supplier getBySupplierCode(String supplierCode) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getSupplierCode, supplierCode);
        return this.getOne(wrapper);
    }
    
    @Override
    public List<Supplier> listByRating(String rating) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getRating, rating);
        return this.list(wrapper);
    }
}
