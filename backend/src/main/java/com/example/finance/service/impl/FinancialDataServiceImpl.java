package com.example.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.FinancialData;
import com.example.finance.mapper.FinancialDataMapper;
import com.example.finance.service.FinancialDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 财务数据服务实现类
 */
@Service
public class FinancialDataServiceImpl extends ServiceImpl<FinancialDataMapper, FinancialData> implements FinancialDataService {

    @Override
    public List<FinancialData> listAll() {
        return this.list();
    }
}
