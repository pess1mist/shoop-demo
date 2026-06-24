package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.PurchasePriceHistory;

import java.util.List;

/**
 * 采购价格历史服务接口
 */
public interface PurchasePriceHistoryService extends IService<PurchasePriceHistory> {

    /**
     * 查询某材料的价格历史
     * @param materialCode 材料编码
     * @return 价格历史列表
     */
    List<PurchasePriceHistory> listByMaterialCode(String materialCode);

    /**
     * 分析价格走势
     * @param materialCode 材料编码
     * @return 价格分析报告（简单实现）
     */
    String analyzePriceTrend(String materialCode);
}
