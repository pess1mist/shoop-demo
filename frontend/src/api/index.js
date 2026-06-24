import request from '@/utils/request'

// ==================== 监控仪表盘 ====================

// 获取监控仪表盘数据
export function getDashboard() {
  return request({
    url: '/monitor/dashboard',
    method: 'get'
  })
}

// 获取部门监控数据
export function getDepartmentMonitor(deptCode) {
  return request({
    url: `/monitor/department/${deptCode}`,
    method: 'get'
  })
}

// ==================== 业务数据管理 ====================

// 业务数据采集
export function collectData(sourceSystem) {
  return request({
    url: '/business/collect',
    method: 'post',
    params: { sourceSystem }
  })
}

// 获取业务数据列表
export function getBusinessList(params) {
  return request({
    url: '/business/list',
    method: 'get',
    params
  })
}

// ==================== 财务数据管理 ====================

// 获取财务数据列表
export function getFinancialList(params) {
  return request({
    url: '/financial/list',
    method: 'get',
    params
  })
}

// ==================== 预算管理 ====================

// 预算检查
export function checkBudget(projectCode, amount) {
  return request({
    url: '/budget/check',
    method: 'get',
    params: { projectCode, amount }
  })
}

// 获取预算执行情况
export function getBudgetExecution(year, item) {
  return request({
    url: '/budget/execution',
    method: 'get',
    params: { year, item }
  })
}

// 获取预算执行列表
export function getBudgetExecutionList(period) {
  return request({
    url: `/api/budget-execution/list/${period}`,
    method: 'get'
  })
}

// 分析预算执行差异
export function analyzeBudgetVariance(budgetItem) {
  return request({
    url: `/api/budget-execution/analyze/${budgetItem}`,
    method: 'get'
  })
}

// ==================== 预测分析 ====================

// 收入预测
export function predictRevenue(months = 6) {
  return request({
    url: '/prediction/revenue',
    method: 'get',
    params: { months }
  })
}

// 成本预测
export function predictCost(months = 6) {
  return request({
    url: '/prediction/cost',
    method: 'get',
    params: { months }
  })
}

// ==================== 预警中心 ====================

// 获取预警信息
export function getAlerts(params) {
  return request({
    url: '/alert/list',
    method: 'get',
    params
  })
}

// ==================== 采购订单管理 ====================

// 查询所有采购订单
export function getPurchaseOrderList() {
  return request({
    url: '/purchase-order/list',
    method: 'get'
  })
}

// 根据供应商查询订单
export function getPurchaseOrderBySupplier(supplierId) {
  return request({
    url: `/purchase-order/list/${supplierId}`,
    method: 'get'
  })
}

// 根据材料查询订单
export function getPurchaseOrderByMaterial(materialCode) {
  return request({
    url: `/purchase-order/list/material/${materialCode}`,
    method: 'get'
  })
}

// 新增采购订单
export function addPurchaseOrder(data) {
  return request({
    url: '/purchase-order/add',
    method: 'post',
    data
  })
}

// 删除采购订单
export function deletePurchaseOrder(orderId) {
  return request({
    url: `/purchase-order/${orderId}`,
    method: 'delete'
  })
}

// 更新采购订单
export function updatePurchaseOrder(data) {
  return request({
    url: '/purchase-order/update',
    method: 'put',
    data
  })
}

// ==================== 采购价格管理 ====================

// 查询价格历史
export function getPriceHistory(materialCode) {
  return request({
    url: `/purchase-price/list/${materialCode}`,
    method: 'get'
  })
}

// 分析价格走势
export function analyzePriceTrend(materialCode) {
  return request({
    url: `/purchase-price/analyze/${materialCode}`,
    method: 'get'
  })
}

// ==================== 产品 BOM 管理 ====================

// 查询产品 BOM
export function getProductBom(productCode) {
  return request({
    url: `/product-bom/list/${productCode}`,
    method: 'get'
  })
}

// 查询材料用于哪些产品
export function getProductBomByMaterial(materialCode) {
  return request({
    url: `/product-bom/list/material/${materialCode}`,
    method: 'get'
  })
}

// ==================== 生产订单管理 ====================

// 查询生产订单列表
export function getProductionOrderList() {
  return request({
    url: '/production-order/list',
    method: 'get'
  })
}

// 查询产品生产订单
export function getProductionOrderByProduct(productCode) {
  return request({
    url: `/production-order/list/${productCode}`,
    method: 'get'
  })
}

// 统计产品成本
export function calculateProductCost(productCode) {
  return request({
    url: `/production-order/cost-summary/${productCode}`,
    method: 'get'
  })
}

// ==================== 制造费用管理 ====================

// 查询生产线费用
export function getManufacturingCostByLine(productionLine) {
  return request({
    url: `/manufacturing-cost/list/${productionLine}`,
    method: 'get'
  })
}

// 查询费用类别
export function getManufacturingCostByCategory(costCategory) {
  return request({
    url: `/manufacturing-cost/list/category/${costCategory}`,
    method: 'get'
  })
}

// 统计生产线总费用
export function calculateTotalCost(productionLine) {
  return request({
    url: `/manufacturing-cost/total/${productionLine}`,
    method: 'get'
  })
}

// ==================== 内控预警管理 ====================

// 查询预警日志
export function getInternalControlList() {
  return request({
    url: '/internal-control/list',
    method: 'get'
  })
}

// 按预警类型查询
export function getInternalControlByType(alertType) {
  return request({
    url: `/internal-control/list/${alertType}`,
    method: 'get'
  })
}

// 按处理状态查询
export function getInternalControlByStatus(handleStatus) {
  return request({
    url: `/internal-control/list/status/${handleStatus}`,
    method: 'get'
  })
}

// 更新预警处理状态
export function updateInternalControlStatus(logId, handleStatus, handler) {
  return request({
    url: '/internal-control/update-status',
    method: 'put',
    params: { logId, handleStatus, handler }
  })
}

// ==================== 成本管理 ====================

// 获取成本趋势
export function getCostTrend(params) {
  return request({
    url: '/cost/trend',
    method: 'get',
    params
  })
}

// 获取成本结构（按年份分组）
export function getCostStructure(params) {
  return request({
    url: '/cost/structure',
    method: 'get',
    params
  })
}

// 获取产品列表
export function getProductList() {
  return request({
    url: '/cost/products',
    method: 'get'
  })
}

// 获取材料价格趋势
export function getPriceTrend(materialCode) {
  return request({
    url: '/cost/priceTrend',
    method: 'get',
    params: { materialCode }
  })
}

// ==================== 毕业设计数据管理 ====================

// 获取所有业务数据概览
export function getGraduationDataOverview() {
  return request({
    url: '/graduation-data/overview',
    method: 'get'
  })
}

// 获取采购订单数据
export function getGraduationPurchaseOrders() {
  return request({
    url: '/graduation-data/purchase-orders',
    method: 'get'
  })
}

// 获取预算执行数据
export function getGraduationBudgetExecutions() {
  return request({
    url: '/graduation-data/budget-executions',
    method: 'get'
  })
}

// 获取制造成本明细
export function getGraduationManufacturingCosts() {
  return request({
    url: '/graduation-data/manufacturing-costs',
    method: 'get'
  })
}

// 获取产品 BOM 数据
export function getGraduationProductBoms() {
  return request({
    url: '/graduation-data/product-boms',
    method: 'get'
  })
}

// 获取生产订单数据
export function getGraduationProductionOrders() {
  return request({
    url: '/graduation-data/production-orders',
    method: 'get'
  })
}

// 获取内控预警日志
export function getGraduationInternalControlLogs() {
  return request({
    url: '/graduation-data/internal-control-logs',
    method: 'get'
  })
}

// 获取业务经营数据
export function getGraduationBusinessData() {
  return request({
    url: '/graduation-data/business-data',
    method: 'get'
  })
}

// 获取财务报表数据
export function getGraduationFinancialData() {
  return request({
    url: '/graduation-data/financial-data',
    method: 'get'
  })
}

// 获取完整数据包
export function getGraduationDataFullPackage() {
  return request({
    url: '/graduation-data/full-package',
    method: 'get'
  })
}

// ==================== 数据分析（5 大核心页面）====================

// 获取数据可视化分析（监控仪表盘）数据
export function getDataVisualization(params) {
  return request({
    url: '/data-analysis/data-viz',
    method: 'get',
    params,
    // 禁用缓存，确保每次请求都获取最新数据
    headers: {
      'Cache-Control': 'no-cache',
      'Pragma': 'no-cache'
    }
  })
}

// 获取成本分析数据
export function getCostAnalysis(params) {
  return request({
    url: '/data-analysis/cost-analysis',
    method: 'get',
    params
  })
}

// 获取预算执行数据
export function getBudgetExecutionData(params) {
  return request({
    url: '/budget-execution/data',
    method: 'get',
    params
  })
}

// 获取生产监控数据
export function getProductionMonitorData(params) {
  return request({
    url: '/data-analysis/production-monitor',
    method: 'get',
    params
  })
}

// 获取内控预警数据
export function getInternalControlData(params) {
  return request({
    url: '/data-analysis/internal-control',
    method: 'get',
    params
  })
}
