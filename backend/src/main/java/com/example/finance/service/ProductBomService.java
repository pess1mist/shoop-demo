package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.ProductBom;

import java.util.List;

/**
 * 产品 BOM 服务接口
 */
public interface ProductBomService extends IService<ProductBom> {

    /**
     * 查询所有产品 BOM
     */
    List<ProductBom> listAll();

    /**
     * 查询某产品的 BOM 清单
     * @param productCode 产品编码
     * @return BOM 清单
     */
    List<ProductBom> listByProductCode(String productCode);

    /**
     * 查询某材料用于哪些产品
     * @param materialCode 材料编码
     * @return 产品列表
     */
    List<ProductBom> listByMaterialCode(String materialCode);
}
