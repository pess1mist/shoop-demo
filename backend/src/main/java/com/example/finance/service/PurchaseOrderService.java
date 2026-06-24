package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.PurchaseOrder;

import java.util.List;

/**
 * 采购订单服务接口
 */
public interface PurchaseOrderService extends IService<PurchaseOrder> {

    /**
     * 查询所有采购订单
     * @return 采购订单列表
     */
    List<PurchaseOrder> listAll();

    /**
     * 根据供应商 ID 查询订单
     * @param supplierId 供应商 ID
     * @return 采购订单列表
     */
    List<PurchaseOrder> listBySupplier(String supplierId);

    /**
     * 根据材料编码查询订单
     * @param materialCode 材料编码
     * @return 采购订单列表
     */
    List<PurchaseOrder> listByMaterial(String materialCode);
}
