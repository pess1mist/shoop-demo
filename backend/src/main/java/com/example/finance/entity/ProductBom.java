package com.example.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 产品 BOM 实体类
 */
@Data
@TableName("product_bom")
public class ProductBom {

    /**
     * BOM ID
     */
    @NotBlank(message = "BOM ID 不能为空")
    @TableId(type = IdType.ASSIGN_ID)
    private String bomId;

    /**
     * 产品编码
     */
    @NotBlank(message = "产品编码不能为空")
    private String productCode;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    /**
     * 材料编码
     */
    @NotBlank(message = "材料编码不能为空")
    private String materialCode;

    /**
     * 材料名称
     */
    @NotBlank(message = "材料名称不能为空")
    private String materialName;

    /**
     * 单耗（吨/吨）
     */
    @NotNull(message = "单耗不能为空")
    @DecimalMin(value = "0", message = "单耗必须大于 0")
    private BigDecimal quantityPerUnit;

    /**
     * 生效日期
     */
    private LocalDate validFrom;
}
