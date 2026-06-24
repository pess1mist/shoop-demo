package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.BudgetMaster;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预算主表 Mapper 接口
 */
@Mapper
public interface BudgetMasterMapper extends BaseMapper<BudgetMaster> {
}
