package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.PurchasePriceHistory;
import com.example.finance.vo.CostTrendVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 采购价格历史 Mapper 接口
 */
@Mapper
public interface PurchasePriceHistoryMapper extends BaseMapper<PurchasePriceHistory> {

    /**
     * 按材料和时间范围查询价格趋势
     */
    @Select("SELECT DATE_FORMAT(order_date, 'yyyy-MM') as month, AVG(unit_price) as cost " +
            "FROM purchase_price_history " +
            "WHERE material_code = #{materialCode} " +
            "AND order_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(order_date, 'yyyy-MM') " +
            "ORDER BY month")
    List<CostTrendVO> selectPriceTrendByDateRange(@Param("materialCode") String materialCode,
                                                   @Param("startDate") String startDate,
                                                   @Param("endDate") String endDate);

    /**
     * 查询最新价格
     */
    @Select("SELECT * FROM purchase_price_history WHERE material_code = #{materialCode} ORDER BY order_date DESC LIMIT 1")
    PurchasePriceHistory selectLatestPrice(@Param("materialCode") String materialCode);
}
