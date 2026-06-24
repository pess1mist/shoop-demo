package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.finance.entity.BusinessData;
import com.example.finance.entity.FinancialData;
import com.example.finance.mapper.BusinessDataMapper;
import com.example.finance.mapper.FinancialDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCollectionService {

    private final BusinessDataMapper businessDataMapper;
    private final FinancialDataMapper financialDataMapper;

    @Transactional(rollbackFor = Exception.class)
    public void collectBusinessData(String sourceSystem) {
        log.info("开始采集业务数据，系统来源：{}", sourceSystem);
        
        BusinessData businessData = new BusinessData();
        businessData.setBusinessCode("BUS" + System.currentTimeMillis());
        businessData.setBusinessType("SALE");
        businessData.setDepartmentCode("DEPT001");
        businessData.setProjectName("项目 A");
        businessData.setAmount(new java.math.BigDecimal("10000.00"));
        businessData.setBusinessDate(LocalDateTime.now());
        businessData.setStatus("COMPLETED");
        businessData.setSourceSystem(sourceSystem);
        
        businessDataMapper.insert(businessData);
        autoGenerateFinancialVoucher(businessData);
        
        log.info("业务数据采集完成：{}", businessData.getBusinessCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void autoGenerateFinancialVoucher(BusinessData businessData) {
        log.info("开始自动生成财务凭证：{}", businessData.getBusinessCode());
        
        FinancialData financialData = new FinancialData();
        financialData.setVoucherCode("VOU" + System.currentTimeMillis());
        financialData.setSubjectCode("6001");
        financialData.setSubjectName("主营业务收入");
        financialData.setDebitAmount(businessData.getAmount());
        financialData.setCreditAmount(java.math.BigDecimal.ZERO);
        financialData.setBalance(businessData.getAmount());
        financialData.setVoucherDate(LocalDateTime.now());
        financialData.setBusinessCode(businessData.getBusinessCode());
        financialData.setBusinessDataId(businessData.getId());
        financialData.setAccountingPeriod(LocalDateTime.now().toString().substring(0, 7));
        financialData.setCostCenter(businessData.getDepartmentCode());
        
        financialDataMapper.insert(financialData);
        log.info("财务凭证生成完成：{}", financialData.getVoucherCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncBusinessToFinancial() {
        log.info("开始业财数据同步");
        
        LambdaQueryWrapper<BusinessData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusinessData::getStatus, "COMPLETED");
        List<BusinessData> businessList = businessDataMapper.selectList(queryWrapper);
        
        for (BusinessData business : businessList) {
            LambdaQueryWrapper<FinancialData> finQuery = new LambdaQueryWrapper<>();
            finQuery.eq(FinancialData::getBusinessCode, business.getBusinessCode());
            FinancialData existing = financialDataMapper.selectOne(finQuery);
            
            if (existing == null) {
                autoGenerateFinancialVoucher(business);
            }
        }
        log.info("业财数据同步完成");
    }
}
