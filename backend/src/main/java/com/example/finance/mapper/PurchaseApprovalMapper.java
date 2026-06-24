package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.PurchaseApproval;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购审批 Mapper 接口
 */
@Mapper
public interface PurchaseApprovalMapper extends BaseMapper<PurchaseApproval> {
}
