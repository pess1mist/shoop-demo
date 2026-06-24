package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品 Mapper 接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
