package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.WorkflowTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程任务 Mapper 接口
 */
@Mapper
public interface WorkflowTaskMapper extends BaseMapper<WorkflowTask> {
}
