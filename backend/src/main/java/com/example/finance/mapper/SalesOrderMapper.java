package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.SalesOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售订单 Mapper 接口
 */
@Mapper
public interface SalesOrderMapper extends BaseMapper<SalesOrder> {
}
