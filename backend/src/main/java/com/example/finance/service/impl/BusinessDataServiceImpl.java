package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.BusinessData;
import com.example.finance.mapper.BusinessDataMapper;
import com.example.finance.service.BusinessDataService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 业务数据服务实现类
 */
@Service
public class BusinessDataServiceImpl extends ServiceImpl<BusinessDataMapper, BusinessData> implements BusinessDataService {

    @Override
    public List<BusinessData> listAll() {
        return this.list();
    }

    @Override
    public List<BusinessData> listByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<BusinessData> wrapper = new LambdaQueryWrapper<>();
        
        // 动态添加查询条件
        if (params != null) {
            // 业务类型条件
            String businessType = (String) params.get("businessType");
            if (StringUtils.hasText(businessType) && !"all".equals(businessType)) {
                wrapper.eq(BusinessData::getBusinessType, businessType);
            }
        }
        
        // 按业务日期降序排列
        wrapper.orderByDesc(BusinessData::getBusinessDate);
        
        return this.list(wrapper);
    }
}
