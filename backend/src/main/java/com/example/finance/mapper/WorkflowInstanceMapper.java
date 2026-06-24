package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.WorkflowInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程实例 Mapper 接口
 */
@Mapper
public interface WorkflowInstanceMapper extends BaseMapper<WorkflowInstance> {
}
