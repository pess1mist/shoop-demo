package com.example.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.finance.entity.InternalControlLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 内控预警日志 Mapper 接口
 */
@Mapper
public interface InternalControlLogMapper extends BaseMapper<InternalControlLog> {

    /**
     * 按状态查询预警日志
     */
    @Select("SELECT * FROM internal_control_log WHERE handle_status = #{status} ORDER BY alert_time DESC")
    List<InternalControlLog> selectByStatus(@Param("status") String status);

    /**
     * 按日期范围查询预警日志
     */
    @Select("SELECT * FROM internal_control_log WHERE alert_time BETWEEN #{startDate} AND #{endDate} ORDER BY alert_time DESC")
    List<InternalControlLog> selectByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 统计各预警类型数量
     */
    @Select("SELECT alert_type, COUNT(*) as count FROM internal_control_log GROUP BY alert_type")
    List<InternalControlLog> selectAlertTypeStats();
}
