package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.ManufacturingCostDetail;

import java.util.List;

/**
 * 制造费用明细服务接口
 */
public interface ManufacturingCostDetailService extends IService<ManufacturingCostDetail> {

    /**
     * 查询所有制造费用明细
     */
    List<ManufacturingCostDetail> listAll();

    /**
     * 按生产线查询费用明细
     * @param productionLine 生产线
     * @return 费用明细列表
     */
    List<ManufacturingCostDetail> listByProductionLine(String productionLine);

    /**
     * 按费用类别查询
     * @param costCategory 费用类别
     * @return 费用明细列表
     */
    List<ManufacturingCostDetail> listByCostCategory(String costCategory);

    /**
     * 统计某生产线的总费用
     * @param productionLine 生产线
     * @return 总费用
     */
    String calculateTotalCost(String productionLine);
}
