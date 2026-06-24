package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.MaterialPriceFluctuation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 材料采购价格波动 Mapper
 */
@Mapper
public interface MaterialPriceFluctuationMapper extends BaseMapper<MaterialPriceFluctuation> {
    
    /**
     * 查询价格波动趋势
     */
    @Select("SELECT material_code, material_name, purchase_date, unit_price, price_change_rate " +
            "FROM material_price_fluctuation " +
            "WHERE material_code = #{materialCode} " +
            "AND purchase_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY purchase_date")
    List<Map<String, Object>> getPriceTrend(@Param("materialCode") String materialCode,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate);
    
    /**
     * 查询预算差异分析
     */
    @Select("SELECT material_code, material_name, accounting_period, " +
            "SUM(budget_amount) as totalBudget, " +
            "SUM(total_amount) as totalActual, " +
            "SUM(budget_variance) as totalVariance " +
            "FROM material_price_fluctuation " +
            "WHERE accounting_period BETWEEN #{startPeriod} AND #{endPeriod} " +
            "GROUP BY material_code, material_name, accounting_period " +
            "ORDER BY accounting_period")
    List<Map<String, Object>> getBudgetVariance(@Param("startPeriod") String startPeriod,
                                                 @Param("endPeriod") String endPeriod);
    
    /**
     * 查询价格波动率统计
     */
    @Select("SELECT material_code, material_name, " +
            "AVG(price_change_rate) as avgChangeRate, " +
            "MAX(price_change_rate) as maxChangeRate, " +
            "MIN(price_change_rate) as minChangeRate " +
            "FROM material_price_fluctuation " +
            "WHERE accounting_period = #{period} " +
            "GROUP BY material_code, material_name")
    List<Map<String, Object>> getFluctuationStatistics(@Param("period") String period);
}
