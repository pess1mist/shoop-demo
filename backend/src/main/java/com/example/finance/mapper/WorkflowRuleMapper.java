package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.WorkflowRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批规则 Mapper
 */
@Mapper
public interface WorkflowRuleMapper extends BaseMapper<WorkflowRule> {
}
