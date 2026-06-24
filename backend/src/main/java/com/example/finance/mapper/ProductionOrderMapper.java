package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.ProductionOrder;
import com.example.finance.vo.CostTrendVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 生产订单 Mapper 接口
 */
@Mapper
public interface ProductionOrderMapper extends BaseMapper<ProductionOrder> {

    /**
     * 按年度统计产品成本（按月）
     */
    @Select("SELECT DATE_FORMAT(end_date, 'yyyy-MM') as month, SUM(total_cost) as cost " +
            "FROM production_order " +
            "WHERE product_code = #{productCode} AND YEAR(end_date) = #{year} " +
            "GROUP BY DATE_FORMAT(end_date, 'yyyy-MM') " +
            "ORDER BY month")
    List<CostTrendVO> selectCostTrendByYear(@Param("productCode") String productCode, @Param("year") Integer year);

    /**
     * 按日期范围统计产品成本（按月）
     */
    @Select("SELECT DATE_FORMAT(end_date, 'yyyy-MM') as month, SUM(total_cost) as cost " +
            "FROM production_order " +
            "WHERE product_code = #{productCode} " +
            "AND end_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(end_date, 'yyyy-MM') " +
            "ORDER BY month")
    List<CostTrendVO> selectCostTrendByDateRange(@Param("productCode") String productCode, 
                                                  @Param("startDate") String startDate, 
                                                  @Param("endDate") String endDate);

    /**
     * 按产品查询生产订单
     */
    @Select("SELECT * FROM production_order WHERE product_code = #{productCode} ORDER BY end_date")
    List<ProductionOrder> selectByProductCode(@Param("productCode") String productCode);
}
