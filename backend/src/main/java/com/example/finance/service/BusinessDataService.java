package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.BusinessData;

import java.util.List;

/**
 * 业务数据服务接口
 */
public interface BusinessDataService extends IService<BusinessData> {
    
    /**
     * 查询所有业务数据
     */
    List<BusinessData> listAll();

    /**
     * 根据条件动态查询业务数据
     * @param params 查询条件（businessType: 业务类型）
     * @return 业务数据列表
     */
    List<BusinessData> listByCondition(java.util.Map<String, Object> params);
}
