package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.WorkflowDefinition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程定义 Mapper 接口
 */
@Mapper
public interface WorkflowDefinitionMapper extends BaseMapper<WorkflowDefinition> {
}
