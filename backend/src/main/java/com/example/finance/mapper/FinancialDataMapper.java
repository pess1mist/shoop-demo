package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.FinancialData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 财务数据 Mapper 接口
 */
@Mapper
public interface FinancialDataMapper extends BaseMapper<FinancialData> {
}
