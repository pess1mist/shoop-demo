package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.FinancialData;

import java.util.List;

/**
 * 财务数据服务接口
 */
public interface FinancialDataService extends IService<FinancialData> {
    
    /**
     * 查询所有财务数据
     */
    List<FinancialData> listAll();
}
