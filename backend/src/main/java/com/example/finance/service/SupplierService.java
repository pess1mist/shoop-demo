package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.Supplier;

import java.util.List;

/**
 * 供应商 Service 接口
 */
public interface SupplierService extends IService<Supplier> {
    
    /**
     * 查询所有供应商
     */
    List<Supplier> listAll();
    
    /**
     * 根据供应商代码查询
     */
    Supplier getBySupplierCode(String supplierCode);
    
    /**
     * 根据评级查询
     */
    List<Supplier> listByRating(String rating);
}
