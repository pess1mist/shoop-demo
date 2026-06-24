package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.Product;

import java.util.List;

/**
 * 产品 Service 接口
 */
public interface ProductService extends IService<Product> {
    
    /**
     * 查询所有产品
     */
    List<Product> listAll();
    
    /**
     * 根据产品代码查询
     */
    Product getByProductCode(String productCode);
    
    /**
     * 根据类别查询
     */
    List<Product> listByCategory(String category);
}
