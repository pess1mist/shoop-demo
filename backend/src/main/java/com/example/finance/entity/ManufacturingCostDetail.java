package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 制造费用明细实体类
 */
@Data
@TableName("manufacturing_cost_detail")
public class ManufacturingCostDetail {

    /**
     * 自增 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 费用日期
     */
    private LocalDate costDate;

    /**
     * 费用类别（水电费/设备折旧/维修保养等）
     */
    private String costCategory;

    /**
     * 金额（元）
     */
    private BigDecimal amount;

    /**
     * 生产线（膨化线/乳化线/水胶线/全厂）
     */
    private String productionLine;

    /**
     * 审批人
     */
    private String approver;
}