import { get, post, put, del, createRestApi } from './apiFactory'

/**
 * 监控仪表盘 API
 */
export const monitorApi = {
  getDashboard: get('/api/monitor/dashboard'),
  getDepartmentMonitor: (deptCode) => get(`/api/monitor/department/${deptCode}`)()
}

/**
 * 业务数据管理 API
 */
export const businessApi = {
  collectData: (sourceSystem) => post('/api/business/collect')({ sourceSystem }),
  getBusinessList: get('/api/business/list')
}

/**
 * 财务数据管理 API
 */
export const financialApi = {
  getFinancialList: get('/api/financial/list')
}

/**
 * 预算管理 API
 */
export const budgetApi = {
  checkBudget: (projectCode, amount) => get('/api/budget/check')({ projectCode, amount }),
  getBudgetExecution: (year, item) => get('/api/budget/execution')({ year, item }),
  getBudgetExecutionList: (period) => get(`/api/budget-execution/list/${period}`)(),
  analyzeBudgetVariance: (budgetItem) => get(`/api/budget-execution/analyze/${budgetItem}`)()
}

/**
 * 预测分析 API
 */
export const predictionApi = {
  predictRevenue: (months = 6) => get('/api/prediction/revenue')({ months }),
  predictCost: (months = 6) => get('/api/prediction/cost')({ months })
}

/**
 * 预警中心 API
 */
export const alertApi = {
  getAlerts: get('/api/alert/list')
}

/**
 * 采购订单管理 API
 */
export const purchaseOrderApi = {
  list: get('/api/purchase-order/list'),
  getBySupplier: (supplierId) => get(`/api/purchase-order/list/${supplierId}`)(),
  getByMaterial: (materialCode) => get(`/api/purchase-order/list/material/${materialCode}`)(),
  add: post('/api/purchase-order/add'),
  delete: (orderId) => del(`/api/purchase-order/${orderId}`)(),
  update: put('/api/purchase-order/update')
}

/**
 * 采购价格管理 API
 */
export const purchasePriceApi = {
  getPriceHistory: (materialCode) => get(`/api/purchase-price/list/${materialCode}`)(),
  analyzePriceTrend: (materialCode) => get(`/api/purchase-price/analyze/${materialCode}`)()
}

/**
 * 产品BOM管理 API
 */
export const productBomApi = {
  getProductBom: (productCode) => get(`/api/product-bom/list/${productCode}`)(),
  getProductBomByMaterial: (materialCode) => get(`/api/product-bom/list/material/${materialCode}`)()
}

/**
 * 生产订单管理 API
 */
export const productionOrderApi = {
  list: get('/api/production-order/list'),
  getByProduct: (productCode) => get(`/api/production-order/list/${productCode}`)(),
  calculateProductCost: (productCode) => get(`/api/production-order/cost-summary/${productCode}`)()
}

/**
 * 制造费用管理 API
 */
export const manufacturingCostApi = {
  getByLine: (productionLine) => get(`/api/manufacturing-cost/list/${productionLine}`)(),
  getByCategory: (costCategory) => get(`/api/manufacturing-cost/list/category/${costCategory}`)(),
  calculateTotalCost: (productionLine) => get(`/api/manufacturing-cost/total/${productionLine}`)()
}

/**
 * 内控预警管理 API
 */
export const internalControlApi = {
  list: get('/api/internal-control/list'),
  getByType: (alertType) => get(`/api/internal-control/list/${alertType}`)(),
  getByStatus: (handleStatus) => get(`/api/internal-control/list/status/${handleStatus}`)(),
  updateStatus: (logId, handleStatus, handler) => 
    put('/api/internal-control/update-status')({ logId, handleStatus, handler })
}

/**
 * 成本管理 API
 */
export const costApi = {
  getPriceTrend: get('/api/cost/priceTrend'),
  getCostStructure: get('/api/cost/costStructure'),
  getCostTrend: get('/api/cost/costTrend')
}

/**
 * 供应商管理 API (使用RESTful API工厂)
 */
export const supplierApi = createRestApi('/api/supplier')

/**
 * 产品管理 API (使用RESTful API工厂)
 */
export const productApi = createRestApi('/api/product')
