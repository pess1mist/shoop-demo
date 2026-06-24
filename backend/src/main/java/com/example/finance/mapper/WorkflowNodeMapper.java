package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.WorkflowNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程节点 Mapper 接口
 */
@Mapper
public interface WorkflowNodeMapper extends BaseMapper<WorkflowNode> {
}
