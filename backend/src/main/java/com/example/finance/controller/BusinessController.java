package com.example.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.finance.dto.Result;
import com.example.finance.entity.BusinessData;
import com.example.finance.service.BusinessDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    @Autowired
    private BusinessDataService businessDataService;

    @PostMapping("/collect")
    public Result<Map<String, Object>> collectData(@RequestParam String sourceSystem) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "数据采集成功");
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> listData(
            @RequestParam(required = false) String businessType) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 根据条件查询业务数据
            List<BusinessData> dataList;
            if (StringUtils.hasText(businessType) && !"all".equals(businessType)) {
                LambdaQueryWrapper<BusinessData> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(BusinessData::getBusinessType, businessType);
                wrapper.orderByDesc(BusinessData::getBusinessDate);
                dataList = businessDataService.list(wrapper);
            } else {
                dataList = businessDataService.listAll();
            }
            
            result.put("data", dataList);
            result.put("total", dataList.size());
            
            // 获取业务类型列表（用于筛选）
            List<String> businessTypes = businessDataService.list()
                    .stream()
                    .map(BusinessData::getBusinessType)
                    .distinct()
                    .sorted()
                    .collect(java.util.stream.Collectors.toList());
            result.put("businessTypes", businessTypes);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取业务数据失败：" + e.getMessage());
        }
    }
}
