package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.AlertWorkOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预警工单 Mapper 接口
 */
@Mapper
public interface AlertWorkOrderMapper extends BaseMapper<AlertWorkOrder> {
}
