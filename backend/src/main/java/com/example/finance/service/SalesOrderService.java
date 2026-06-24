package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.SalesOrder;

import java.util.List;

/**
 * 销售订单服务接口
 */
public interface SalesOrderService extends IService<SalesOrder> {

    /**
     * 查询所有销售订单
     * @return 销售订单列表
     */
    List<SalesOrder> listAll();

    /**
     * 根据客户 ID 查询订单
     * @param customerId 客户 ID
     * @return 销售订单列表
     */
    List<SalesOrder> listByCustomer(String customerId);

    /**
     * 根据产品编码查询订单
     * @param productCode 产品编码
     * @return 销售订单列表
     */
    List<SalesOrder> listByProduct(String productCode);
}
