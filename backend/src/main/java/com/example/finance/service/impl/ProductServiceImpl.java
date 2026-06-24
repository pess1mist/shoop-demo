package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.Product;
import com.example.finance.mapper.ProductMapper;
import com.example.finance.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 产品 Service 实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Override
    public List<Product> listAll() {
        return this.list();
    }
    
    @Override
    public Product getByProductCode(String productCode) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getProductCode, productCode);
        return this.getOne(wrapper);
    }
    
    @Override
    public List<Product> listByCategory(String category) {
        // 由于产品表中没有category字段，返回空列表
        return new ArrayList<>();
    }
}
