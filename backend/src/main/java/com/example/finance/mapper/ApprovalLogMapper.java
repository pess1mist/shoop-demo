package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.ApprovalLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批记录 Mapper
 */
@Mapper
public interface ApprovalLogMapper extends BaseMapper<ApprovalLog> {
}
