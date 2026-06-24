package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.ProductBom;
import com.example.finance.mapper.ProductBomMapper;
import com.example.finance.service.ProductBomService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品 BOM 服务实现类
 */
@Service
public class ProductBomServiceImpl extends ServiceImpl<ProductBomMapper, ProductBom> implements ProductBomService {

    @Override
    public List<ProductBom> listAll() {
        return this.list();
    }

    @Override
    @Cacheable(value = "productBom", key = "#productCode")
    public List<ProductBom> listByProductCode(String productCode) {
        LambdaQueryWrapper<ProductBom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductBom::getProductCode, productCode);
        return this.list(wrapper);
    }

    @Override
    @Cacheable(value = "productBom", key = "'material:' + #materialCode")
    public List<ProductBom> listByMaterialCode(String materialCode) {
        LambdaQueryWrapper<ProductBom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductBom::getMaterialCode, materialCode);
        return this.list(wrapper);
    }
}
