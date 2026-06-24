package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.BudgetExecution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 预算执行 Mapper 接口
 */
@Mapper
public interface BudgetExecutionMapper extends BaseMapper<BudgetExecution> {

    /**
     * 按期间查询预算执行
     */
    @Select("SELECT * FROM budget_execution WHERE fiscal_year = #{year} AND period = #{item} ORDER BY period")
    List<BudgetExecution> selectByYearAndItem(@Param("year") Integer year, @Param("item") String item);

    /**
     * 按年份查询预算执行
     */
    @Select("SELECT * FROM budget_execution WHERE fiscal_year = #{year} ORDER BY period")
    List<BudgetExecution> selectByYear(@Param("year") Integer year);

    /**
     * 按日期范围查询预算执行
     */
    @Select("SELECT * FROM budget_execution WHERE period BETWEEN #{startDate} AND #{endDate} ORDER BY period")
    List<BudgetExecution> selectByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
