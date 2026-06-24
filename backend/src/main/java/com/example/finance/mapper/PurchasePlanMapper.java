package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.PurchasePlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购计划 Mapper 接口
 */
@Mapper
public interface PurchasePlanMapper extends BaseMapper<PurchasePlan> {
}
