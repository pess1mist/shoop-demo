package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.BusinessData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 业务数据 Mapper 接口
 */
@Mapper
public interface BusinessDataMapper extends BaseMapper<BusinessData> {
}
