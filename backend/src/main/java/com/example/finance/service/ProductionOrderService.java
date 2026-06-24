package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.ProductionOrder;

import java.util.List;

/**
 * 生产订单服务接口
 */
public interface ProductionOrderService extends IService<ProductionOrder> {

    /**
     * 查询所有生产订单
     * @return 生产订单列表
     */
    List<ProductionOrder> listAll();

    /**
     * 根据产品编码查询订单
     * @param productCode 产品编码
     * @return 生产订单列表
     */
    List<ProductionOrder> listByProductCode(String productCode);

    /**
     * 统计某产品的成本
     * @param productCode 产品编码
     * @return 成本统计信息
     */
    String calculateCostSummary(String productCode);

    /**
     * 根据条件动态查询生产订单
     * @param params 查询条件（line: 生产线，status: 状态）
     * @return 生产订单列表
     */
    List<ProductionOrder> listByCondition(java.util.Map<String, Object> params);
}
