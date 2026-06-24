<template>
  <div class="cost-analysis-container">
    <!-- 顶部品牌栏 -->
    <div class="header-bar">
      <div class="breadcrumb">
        <span>🏭 智能制造内控管理系统</span>
        <span style="margin: 0 8px">></span>
        <span>成本分析</span>
      </div>
      <div class="user-info">
        <el-avatar :size="32" style="margin-right: 8px">用户</el-avatar>
        <span>管理员</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar" v-loading="loading" element-loading-text="数据加载中..." element-loading-background="rgba(255, 255, 255, 0.8)">
      <el-space>
        <el-tag type="success" size="large" effect="dark">
          <el-icon><TrendCharts /></el-icon>
          实时累计数据
        </el-tag>
        <span style="margin-left: 16px">产品：</span>
        <el-select v-model="selectedProduct" placeholder="请选择" style="width: 200px" @change="loadData">
          <el-option label="全部产品" value="all" />
          <el-option 
            v-for="product in productList" 
            :key="product.productCode"
            :label="product.productName" 
            :value="product.productCode" 
          />
        </el-select>
        <el-button type="success" size="small" @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报表
        </el-button>
      </el-space>
    </div>

    <!-- ✅ KPI 指标卡片 -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon :size="32"><Money /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">总成本</div>
            <div class="kpi-value">{{ kpiData.totalCost }}</div>
            <div class="kpi-unit">万元</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon :size="32"><PriceTag /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">单位成本</div>
            <div class="kpi-value">{{ kpiData.unitCost }}</div>
            <div class="kpi-unit">元/吨</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon :size="32"><TrendCharts /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">材料费占比</div>
            <div class="kpi-value">{{ kpiData.materialRatio }}</div>
            <div class="kpi-unit">%</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon :size="32"><DArrowRight /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">成本降低率</div>
            <div class="kpi-value" :style="{color: kpiData.costReductionRate < 0 ? '#67C23A' : '#F56C6C'}">
              {{ kpiData.costReductionRate }}
            </div>
            <div class="kpi-unit">% vs 上期</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区 -->
    <el-row :gutter="16" class="main-content">
      <!-- 左侧：各产品单位成本对比（环形图） -->
      <el-col :xs="24" :sm="24" :md="24" :lg="8">
        <el-card shadow="hover" class="cost-compare-card">
          <template #header>
            <div class="card-header">
              <span>📊 各产品单位成本对比</span>
              <el-tag type="info" size="small">累计至今</el-tag>
            </div>
          </template>
          <!-- ✅ 环形图展示各产品成本对比 -->
          <div ref="costPieChartRef" class="cost-pie-chart"></div>

          <!-- ✅ 成本明细表（可折叠） -->
          <div class="cost-table-section">
            <div class="table-header" @click="tableCollapsed = !tableCollapsed" style="cursor: pointer; display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: #f5f7fa; border-radius: 4px; margin-bottom: 12px;">
              <div style="display: flex; align-items: center; gap: 8px;">
                <el-icon :size="16" style="transition: transform 0.3s;" :style="{ transform: tableCollapsed ? 'rotate(-90deg)' : 'rotate(0deg)' }">
                  <ArrowDown />
                </el-icon>
                <span style="font-size: 16px; font-weight: 600;">📋 成本明细表</span>
              </div>
              <el-button type="primary" size="small" @click.stop="exportCostTable">
                <el-icon><Download /></el-icon>
                导出
              </el-button>
            </div>
            <el-collapse-transition>
              <div v-show="!tableCollapsed">
                <el-table :data="costTableData" stripe size="small" show-summary :summary-method="getSummaries">
                  <template #empty>
                    <el-empty description="暂无成本数据" :image-size="80" />
                  </template>
                  <el-table-column prop="year" label="月份" width="90" />
                  <el-table-column prop="material" label="材料费(万)" width="90" />
                  <el-table-column prop="labor" label="人工费(万)" width="90" />
                  <el-table-column prop="manufacturing" label="制造费(万)" width="90" />
                  <el-table-column prop="total" label="总计(万)" width="90" />
                </el-table>
              </div>
            </el-collapse-transition>
          </div>
        </el-card>
      </el-col>

      <!-- 中间：完全成本结构变化（堆叠柱状图） -->
      <el-col :xs="24" :sm="24" :md="24" :lg="10">
        <el-card shadow="hover" class="cost-structure-card">
          <template #header>
            <div class="card-header">
              <span>📈 完全成本结构趋势（面积堆叠图）</span>
              <el-tag :type="trendType" size="small">{{ trendText }}</el-tag>
            </div>
          </template>
          <!-- ✅ 面积堆叠图展示月度成本结构 -->
          <div ref="structureChartRef" class="structure-chart" style="height: 380px;"></div>
          
          <!-- 空状态提示 -->
          <el-empty v-if="costStructureData.length === 0" description="暂无成本结构数据" :image-size="120">
            <template #description>
              <p style="color: #909399; font-size: 14px">请检查生产订单数据是否完整</p>
            </template>
          </el-empty>

          <!-- 趋势说明 -->
          <div class="trend-note">
            <el-alert
              type="info"
              :closable="false"
              show-icon
            >
              <template #title>
                {{ trendNote }}
              </template>
            </el-alert>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：成本趋势分析 -->
      <el-col :xs="24" :sm="24" :md="24" :lg="6">
        <el-card shadow="hover" class="trend-analysis-card">
          <template #header>
            <div class="card-header">
              <span>📉 成本趋势分析</span>
              <el-tag :type="costTrendType" size="small">{{ costTrendText }}</el-tag>
            </div>
          </template>
          <div ref="trendChartRef" class="trend-chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, computed, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'
import { Download, TrendCharts, Money, PriceTag, DArrowRight, Refresh, ArrowDown } from '@element-plus/icons-vue'
import { getCostStructure, getProductList, getCostTrend } from '@/api'

// 筛选条件
const selectedProduct = ref('all')
const loading = ref(false)

// ✅ 性能优化：数据缓存
const dataCache = ref(new Map())
const CACHE_EXPIRY_TIME = 5 * 60 * 1000 // 5分钟过期
let loadTimer = null // 防抖定时器

// 产品列表
const productList = ref([])

// 成本明细表数据
const costTableData = ref([])

// ✅ 成本明细表折叠状态（默认折叠）
const tableCollapsed = ref(true)

// 产品单位成本数据
const productUnitCosts = ref([])

// 成本结构数据
const costStructureData = ref([])

// ✅ KPI 指标数据
const kpiData = ref({
  totalCost: 0,
  unitCost: 0,
  materialRatio: 0,
  costReductionRate: 0
})

// 图表引用
const trendChartRef = ref(null)
let trendChart = null

// ✅ 环形图引用
const costPieChartRef = ref(null)
let costPieChart = null

// ✅ 成本结构堆叠柱状图引用
const structureChartRef = ref(null)
let structureChart = null

// 趋势分析（基于堆叠柱状图数据）
const trendType = computed(() => {
  if (costStructureData.value.length < 2) return 'info'
  const first = costStructureData.value[0]
  const last = costStructureData.value[costStructureData.value.length - 1]
  if (first && last && first.materialPercent && last.materialPercent) {
    return last.materialPercent > first.materialPercent ? 'danger' : 'success'
  }
  return 'info'
})

const trendText = computed(() => {
  if (costStructureData.value.length < 2) return '数据加载中'
  const first = costStructureData.value[0]
  const last = costStructureData.value[costStructureData.value.length - 1]
  if (first && last && first.materialPercent && last.materialPercent) {
    return last.materialPercent > first.materialPercent ? '材料费上升' : '材料费下降'
  }
  return '数据正常'
})

const trendNote = computed(() => {
  if (costStructureData.value.length < 2) return '数据加载中...'
  const totalCosts = costStructureData.value.map(d => (d.totalCost / 10000).toFixed(2))
  return `月度总成本趋势：${totalCosts.slice(0, 6).join(' → ')}...（最近${costStructureData.value.length}个月）`
})

const costTrendType = ref('info')
const costTrendText = ref('加载中')

// 加载产品列表
const loadProductList = async () => {
  try {
    const response = await getProductList()
    const data = response?.data ?? response
    if (Array.isArray(data)) {
      productList.value = data
    }
  } catch (error) {
    console.error('加载产品列表失败:', error)
    // 使用默认产品列表，确保前端页面正常显示
    productList.value = [
      { productCode: 'P01', productName: '产品A' },
      { productCode: 'P02', productName: '产品B' },
      { productCode: 'P03', productName: '产品C' }
    ]
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    console.log('🚀 开始加载成本数据...')
    
    // ✅ 检查缓存
    const cacheKey = `cost_${selectedProduct.value}`
    const cached = dataCache.value.get(cacheKey)
    
    if (cached && (Date.now() - cached.timestamp) < CACHE_EXPIRY_TIME) {
      console.log('使用缓存数据')
      const structureData = cached.data
      
      // 直接使用缓存数据
      costStructureData.value = structureData
      costTableData.value = structureData.map(item => ({
        year: item.month || `${item.year}年`,
        material: (item.materialCost / 10000).toFixed(2),
        labor: (item.laborCost / 10000).toFixed(2),
        manufacturing: (item.overheadCost / 10000).toFixed(2),
        total: (item.totalCost / 10000).toFixed(2),
        unitCost: item.unitCost ? item.unitCost.toFixed(2) : '0.00'
      }))
      calculateKPI(structureData)
      renderCostPieChart()
      renderStructureChart()
      await loadCostTrendChart()
      ElMessage.success('数据已更新（缓存）')
      loading.value = false
      return
    }
    
    console.log('📡 请求后端API获取成本数据...')
    
    // ✅ 请求成本结构数据
    const structureResponse = await getCostStructure({
      product: selectedProduct.value
    })
    
    console.log('📥 API响应:', structureResponse)
    
    // ✅ 修复：axios响应拦截器已返回 res.data，直接就是数组
    const structureData = structureResponse
    
    console.log('📊 提取的成本数据:', structureData)
    console.log('📊 数据类型:', typeof structureData, Array.isArray(structureData))
    console.log('📊 数据长度:', Array.isArray(structureData) ? structureData.length : '非数组')
    
    // 处理成本结构数据（唯一数据源）
    if (Array.isArray(structureData) && structureData.length > 0) {
      console.log('✅ 开始处理成本数据，共', structureData.length, '条')
      costStructureData.value = structureData
      console.log('✅ 成本结构数据已赋值')
      
      // ✅ 构建成本明细表
      costTableData.value = structureData.map(item => ({
        year: item.month || `${item.year}年`,
        material: (item.materialCost / 10000).toFixed(2),
        labor: (item.laborCost / 10000).toFixed(2),
        manufacturing: (item.overheadCost / 10000).toFixed(2),
        total: (item.totalCost / 10000).toFixed(2),
        unitCost: item.unitCost ? item.unitCost.toFixed(2) : '0.00'
      }))
      
      console.log('✅ 成本明细表已构建，共', costTableData.value.length, '条')
      
      // ✅ 计算 KPI 指标
      calculateKPI(structureData)
      
      console.log('✅ KPI指标已计算:', kpiData.value)
      
      // ✅ 存入缓存
      dataCache.value.set(cacheKey, {
        data: structureData,
        timestamp: Date.now()
      })
      console.log('✅ 数据已存入缓存')
    } else {
      console.error('❌ 成本结构数据为空或不是数组')
      console.error('❌ structureData:', structureData)
      // 空数据处理
      costStructureData.value = []
      costTableData.value = []
      kpiData.value = {
        totalCost: 0,
        unitCost: 0,
        materialRatio: 0,
        costReductionRate: 0
      }
    }
    
    // ✅ 渲染成本构成环形图
    await nextTick() // 等待DOM更新
    renderCostPieChart()
    
    // ✅ 渲染成本结构堆叠面积图
    await nextTick() // 等待DOM更新
    console.log('🎨 准备渲染面积堆叠图，数据长度:', costStructureData.value.length)
    renderStructureChart()
    
    // 加载成本趋势图表
    await loadCostTrendChart()
    
    ElMessage.success('数据已更新')
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// ✅ 计算移动平均线
const calculateMovingAverage = (data, period) => {
  const result = []
  for (let i = 0; i < data.length; i++) {
    if (i < period - 1) {
      // 前几个数据点不足period个，设为null
      result.push(null)
    } else {
      // 计算最近period个数据的平均值
      let sum = 0
      for (let j = 0; j < period; j++) {
        sum += data[i - j]
      }
      result.push(parseFloat((sum / period).toFixed(2)))
    }
  }
  return result
}

// 加载成本趋势图表
const loadCostTrendChart = async () => {
  if (!trendChartRef.value) return
  
  try {
    // 获取成本趋势数据 - 查询所有历史数据
    const response = await getCostTrend({
      startDate: null,
      endDate: null,
      productCode: selectedProduct.value === 'all' ? null : selectedProduct.value
    })
    const trendData = response?.data ?? response
    
    if (Array.isArray(trendData)) {
      if (!trendChart) {
        trendChart = echarts.init(trendChartRef.value)
      }
      
      // ✅ 直接使用月度数据，不按年汇总
      const months = trendData.map(item => item.month).sort()
      const costs = trendData.map(item => (item.cost / 10000).toFixed(2)) // 转换为万元
      const unitCosts = trendData.map(item => item.unitCost.toFixed(2)) // 单位成本 (元/吨)
            
      // 计算趋势 (比较第一个月和最后一个月的单位成本)
      if (unitCosts.length >= 2) {
        const firstCost = parseFloat(unitCosts[0])
        const lastCost = parseFloat(unitCosts[unitCosts.length - 1])
        if (lastCost > firstCost) {
          costTrendType.value = 'danger'
          costTrendText.value = '↑ 成本上升'
        } else {
          costTrendType.value = 'success'
          costTrendText.value = '↓ 成本下降'
        }
      }
            
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: (params) => {
            const month = params[0].name
            const totalCost = params[0].value
            const unitCost = params[1].value
            return `${month}<br/>总成本：${totalCost}万元<br/>单位成本：${unitCost}元/吨`
          }
        },
        legend: {
          data: ['总成本', '单位成本', '移动平均线'],
          top: 10
        },
        xAxis: {
          type: 'category',
          data: months,
          axisLabel: {
            rotate: 45,
            interval: Math.floor(months.length / 12) // 每几个月显示一个标签
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '总成本 (万元)',
            position: 'left'
          },
          {
            type: 'value',
            name: '单位成本 (元/吨)',
            position: 'right'
          }
        ],
        series: [
          {
            name: '总成本',
            type: 'line',
            data: costs,
            smooth: true,
            itemStyle: { color: '#409EFF' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
              ])
            },
            // ✅ 添加标注点：最高值、最低值、平均值
            markPoint: {
              data: [
                { type: 'max', name: '最高值' },
                { type: 'min', name: '最低值' },
                { type: 'average', name: '平均值' }
              ]
            },
            markLine: {
              data: [
                { type: 'average', name: '平均值' }
              ]
            }
          },
          {
            name: '单位成本',
            type: 'line',
            yAxisIndex: 1,
            data: unitCosts,
            smooth: true,
            itemStyle: { color: '#67C23A' }
          },
          // ✅ 添加移动平均线（3期）
          {
            name: '移动平均线',
            type: 'line',
            data: calculateMovingAverage(costs, 3),
            smooth: true,
            itemStyle: { color: '#E6A23C' },
            lineStyle: {
              type: 'dashed',
              width: 2
            },
            symbol: 'none'
          }
        ]
      }
      
      trendChart.setOption(option)
    }
  } catch (error) {
    console.error('加载成本趋势图表失败:', error)
  }
}

// ✅ 导出成本明细表
const exportCostTable = () => {
  if (!costTableData.value || costTableData.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  // 准备导出数据
  const exportData = costTableData.value.map(item => ({
    '月份': item.year,
    '材料费(万元)': parseFloat(item.material),
    '人工费(万元)': parseFloat(item.labor),
    '制造费(万元)': parseFloat(item.manufacturing),
    '总成本(万元)': parseFloat(item.total)
  }))
  
  // 创建工作簿
  const ws = XLSX.utils.json_to_sheet(exportData)
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '成本明细')
  
  // 生成 Excel文件
  const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
  const blob = new Blob([excelBuffer], { type: 'application/octet-stream' })
  
  // 下载文件
  const fileName = `成本分析报表_${new Date().toISOString().slice(0, 10)}.xlsx`
  saveAs(blob, fileName)
  
  ElMessage.success(`已导出 ${exportData.length} 条记录`)
}

// ✅ 导出完整报表（多sheet）
const exportReport = () => {
  if (!costStructureData.value || costStructureData.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  const wb = XLSX.utils.book_new()
  
  // Sheet 1: KPI概览
  const kpiSheetData = [
    { '指标': '总成本', '数值': kpiData.value.totalCost, '单位': '万元' },
    { '指标': '单位成本', '数值': kpiData.value.unitCost, '单位': '元/吨' },
    { '指标': '材料费占比', '数值': kpiData.value.materialRatio, '单位': '%' },
    { '指标': '成本变化率', '数值': kpiData.value.costReductionRate, '单位': '%' }
  ]
  const wsKPI = XLSX.utils.json_to_sheet(kpiSheetData)
  XLSX.utils.book_append_sheet(wb, wsKPI, 'KPI概览')
  
  // Sheet 2: 成本明细
  const detailData = costTableData.value.map(item => ({
    '月份': item.year,
    '材料费(万元)': parseFloat(item.material),
    '人工费(万元)': parseFloat(item.labor),
    '制造费(万元)': parseFloat(item.manufacturing),
    '总成本(万元)': parseFloat(item.total)
  }))
  const wsDetail = XLSX.utils.json_to_sheet(detailData)
  XLSX.utils.book_append_sheet(wb, wsDetail, '成本明细')
  
  // Sheet 3: 月度趋势
  const trendData = costStructureData.value.map(item => ({
    '月份': item.month || `${item.year}年`,
    '材料费(万元)': (item.materialCost / 10000).toFixed(2),
    '人工费(万元)': (item.laborCost / 10000).toFixed(2),
    '制造费(万元)': (item.overheadCost / 10000).toFixed(2),
    '总成本(万元)': (item.totalCost / 10000).toFixed(2),
    '单位成本(元/吨)': item.unitCost ? item.unitCost.toFixed(2) : '0.00'
  }))
  const wsTrend = XLSX.utils.json_to_sheet(trendData)
  XLSX.utils.book_append_sheet(wb, wsTrend, '月度趋势')
  
  // 导出文件
  const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
  const blob = new Blob([excelBuffer], { type: 'application/octet-stream' })
  const fileName = `成本分析完整报表_${new Date().toISOString().slice(0, 10)}.xlsx`
  saveAs(blob, fileName)
  
  ElMessage.success('完整报表已导出（包含3个sheet）')
}

// ✅ 计算 KPI 指标
const calculateKPI = (structureData) => {
  if (!structureData || structureData.length === 0) {
    kpiData.value = {
      totalCost: 0,
      unitCost: 0,
      materialRatio: 0,
      costReductionRate: 0
    }
    return
  }
  
  // 计算总成本（万元）
  const totalCost = structureData.reduce((sum, item) => sum + item.totalCost, 0) / 10000
  kpiData.value.totalCost = totalCost.toFixed(2)
  
  // ✅ 计算加权平均单位成本（元/吨）= 总成本之和 / 总产量之和
  const totalCostSum = structureData.reduce((sum, item) => sum + item.totalCost, 0)
  const totalQuantitySum = structureData.reduce((sum, item) => sum + (item.totalQuantity || 0), 0)
  
  if (totalQuantitySum > 0) {
    kpiData.value.unitCost = (totalCostSum / totalQuantitySum).toFixed(2)
  } else {
    kpiData.value.unitCost = '0.00'
  }
  
  // 计算材料费占比（最新月份）
  const latestData = structureData[structureData.length - 1]
  if (latestData && latestData.totalCost > 0) {
    const materialRatio = (latestData.materialCost / latestData.totalCost * 100).toFixed(1)
    kpiData.value.materialRatio = materialRatio
  }
  
  // 计算成本变化率（对比上一期）
  if (structureData.length >= 2) {
    const current = structureData[structureData.length - 1].totalCost
    const previous = structureData[structureData.length - 2].totalCost
    if (previous > 0) {
      const changeRate = ((current - previous) / previous * 100).toFixed(2)
      kpiData.value.costReductionRate = changeRate
    } else {
      kpiData.value.costReductionRate = '0.00'
    }
  } else {
    kpiData.value.costReductionRate = '0.00'
  }
}

// ✅ 渲染成本构成环形图
const renderCostPieChart = () => {
  if (!costPieChartRef.value) return
  
  // ✅ 空数据检查
  if (!costStructureData.value || costStructureData.value.length === 0) {
    console.warn('成本结构数据为空，跳过环形图渲染')
    return
  }
  
  if (!costPieChart) {
    costPieChart = echarts.init(costPieChartRef.value)
  } else {
    costPieChart.clear() // 先清空再设置新option
  }
  
  // 使用最新月份的成本结构数据
  const latestData = costStructureData.value.length > 0 
    ? costStructureData.value[costStructureData.value.length - 1]
    : { materialCost: 0, laborCost: 0, overheadCost: 0 }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}万 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      data: ['材料费', '人工费', '制造费'],
      textStyle: { fontSize: 11 }
    },
    series: [
      {
        name: '成本构成',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          formatter: '{b}\n{d}%',
          fontSize: 11
        },
        labelLine: {
          show: true,
          length: 15,
          length2: 10
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 13,
            fontWeight: 'bold'
          }
        },
        data: [
          { 
            value: (latestData.materialCost / 10000).toFixed(2), 
            name: '材料费', 
            itemStyle: { color: '#409EFF' } 
          },
          { 
            value: (latestData.laborCost / 10000).toFixed(2), 
            name: '人工费', 
            itemStyle: { color: '#67C23A' } 
          },
          { 
            value: (latestData.overheadCost / 10000).toFixed(2), 
            name: '制造费', 
            itemStyle: { color: '#E6A23C' } 
          }
        ]
      }
    ]
  }
  
  costPieChart.setOption(option)
  
  // ✅ 添加点击事件
  costPieChart.off('click') // 先移除旧的事件监听
  costPieChart.on('click', function(params) {
    ElMessage.info(`${params.name}：${params.value}万元，占比${params.percent}%`)
  })
}

// ✅ 渲染成本结构堆叠面积图
const renderStructureChart = async () => {
  console.log('🎨 ========== 开始渲染面积堆叠图 ==========')
  console.log('📊 costStructureData.value:', costStructureData.value)
  console.log('📊 costStructureData.value.length:', costStructureData.value?.length)
  console.log('📊 costStructureData.value类型:', typeof costStructureData.value)
  console.log('📊 是否为数组:', Array.isArray(costStructureData.value))
  
  // 等待DOM更新
  await nextTick()
  
  if (!structureChartRef.value) {
    console.error('❌ 图表容器ref未就绪！')
    console.log('📦 structureChartRef.value:', structureChartRef.value)
    return
  }
  
  if (costStructureData.value.length === 0) {
    console.warn('⚠️ 数据为空，显示空状态')
    return
  }
  
  console.log('✅ 容器存在，尺寸:', structureChartRef.value.offsetWidth, 'x', structureChartRef.value.offsetHeight)
  
  // 销毁旧实例
  if (structureChart) {
    structureChart.dispose()
    structureChart = null
  }
  
  // 创建新实例
  structureChart = echarts.init(structureChartRef.value)
  console.log('✅ ECharts实例已创建')
  
  // ✅ 按月份排序，确保时间顺序正确
  const sortedData = [...costStructureData.value].sort((a, b) => {
    return (a.month || '').localeCompare(b.month || '')
  })
  
  // 使用月度数据
  const months = sortedData.map(item => item.month || `${item.year}年`)
  const materialCosts = sortedData.map(item => parseFloat((item.materialCost / 10000).toFixed(2)))
  const laborCosts = sortedData.map(item => parseFloat((item.laborCost / 10000).toFixed(2)))
  const overheadCosts = sortedData.map(item => parseFloat((item.overheadCost / 10000).toFixed(2)))
  
  console.log('📈 图表数据:', { months, materialCosts, laborCosts, overheadCosts })
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      formatter: function(params) {
        let result = `<div style="padding: 8px">
          <strong>${params[0].name}</strong><br/>`
        let total = 0
        params.forEach(item => {
          result += `${item.marker}${item.seriesName}: <b>${item.value}万元</b><br/>`
          total += item.value
        })
        result += `<hr style="margin: 8px 0; border: none; border-top: 1px solid #eee"/>
          <strong>总计: ${total.toFixed(2)}万元</strong>
        </div>`
        return result
      }
    },
    legend: {
      data: ['材料费', '人工费', '制造费'],
      top: 10,
      textStyle: {
        fontSize: 12
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false, // ✅ 面积图不需要边界留白
      data: months,
      axisLabel: {
        rotate: 45,
        interval: Math.floor(months.length / 12)
      }
    },
    yAxis: {
      type: 'value',
      name: '金额 (万元)',
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '材料费',
        type: 'line', // ✅ 改为折线图
        stack: 'total', // ✅ 堆叠
        smooth: true, // ✅ 平滑曲线
        emphasis: { focus: 'series' },
        data: materialCosts,
        areaStyle: { // ✅ 添加面积填充
          opacity: 0.8,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.9)' },
            { offset: 1, color: 'rgba(118, 75, 162, 0.4)' }
          ])
        },
        lineStyle: {
          width: 2,
          color: '#667eea'
        },
        itemStyle: {
          color: '#667eea'
        }
      },
      {
        name: '人工费',
        type: 'line',
        stack: 'total',
        smooth: true,
        emphasis: { focus: 'series' },
        data: laborCosts,
        areaStyle: {
          opacity: 0.8,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(240, 147, 251, 0.9)' },
            { offset: 1, color: 'rgba(245, 87, 108, 0.4)' }
          ])
        },
        lineStyle: {
          width: 2,
          color: '#f093fb'
        },
        itemStyle: {
          color: '#f093fb'
        }
      },
      {
        name: '制造费',
        type: 'line',
        stack: 'total',
        smooth: true,
        emphasis: { focus: 'series' },
        data: overheadCosts,
        areaStyle: {
          opacity: 0.8,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(79, 172, 254, 0.9)' },
            { offset: 1, color: 'rgba(0, 242, 254, 0.4)' }
          ])
        },
        lineStyle: {
          width: 2,
          color: '#4facfe'
        },
        itemStyle: {
          color: '#4facfe'
        }
      }
    ]
  }
  
  structureChart.setOption(option)
  
  // ✅ 强制resize确保图表正确显示
  setTimeout(() => {
    structureChart.resize()
  }, 100)
  
  console.log('✅ 面积堆叠图渲染完成')
  
  // ✅ 添加点击事件
  structureChart.off('click')
  structureChart.on('click', function(params) {
    const month = params.name
    const costType = params.seriesName
    const amount = params.value
    ElMessage.info(`${month} - ${costType}：${amount}万元`)
  })
}

// ✅ 表格汇总方法
const getSummaries = (param) => {
  const { columns, data } = param
  const sums = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '总计'
      return
    }
    const values = data.map(item => Number(item[column.property]))
    if (!values.every(value => isNaN(value))) {
      sums[index] = values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0).toFixed(2)
    } else {
      sums[index] = '-'
    }
  })
  return sums
}

// 页面加载时获取数据
onMounted(async () => {
  await loadProductList()
  await loadData()
  // 添加窗口大小改变事件监听器
  window.addEventListener('resize', handleResize)
})

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  trendChart?.resize()
  costPieChart?.resize()
  structureChart?.resize()
}

// 清理
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例
  trendChart?.dispose()
  costPieChart?.dispose()
  structureChart?.dispose()
})

// 监听产品变化，自动重新加载数据
watch(selectedProduct, () => {
  console.log('产品变化，重新加载成本数据...')
  
  // ✅ 防抖处理：500ms 内只触发最后一次请求
  if (loadTimer) {
    clearTimeout(loadTimer)
  }
  loadTimer = setTimeout(() => {
    loadData()
    loadTimer = null
  }, 500)
}, { deep: true })
</script>

<style scoped>
.cost-analysis-container {
  padding: 16px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 顶部品牌栏 */
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.breadcrumb {
  font-size: 16px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  font-size: 14px;
}

/* 筛选栏 */
.filter-bar {
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* ✅ KPI 指标卡片 */
.kpi-row {
  margin-bottom: 16px;
}

.kpi-card {
  display: flex;
  align-items: center;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.kpi-card:hover {
  transform: translateY(-4px);
}

.kpi-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
  flex-shrink: 0;
}

.kpi-content {
  flex: 1;
}

.kpi-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.kpi-unit {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 主要内容区 */
.main-content {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 成本对比卡片 */
.cost-compare-card {
  height: auto;
  min-height: 500px;
}

/* ✅ 环形图样式 */
.cost-pie-chart {
  width: 100%;
  height: 250px;
  margin-bottom: 16px;
}

.product-value {
  width: 100px;
  text-align: right;
  font-weight: bold;
  color: #303133;
}

.cost-table-section {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.section-title {
  font-weight: bold;
  margin-bottom: 12px;
  color: #303133;
}

/* 成本结构卡片 */
.cost-structure-card {
  height: 500px;
}

.structure-chart {
  height: 380px;
  padding: 16px 0;
}

.year-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.year-label {
  width: 80px;
  font-weight: bold;
  color: #303133;
}

.stacked-bar {
  flex: 1;
  height: 32px;
  background: #f0f0f0;
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  margin: 0 12px;
}

.stacked-segment {
  height: 100%;
  transition: width 0.3s;
}

.year-total {
  width: 50px;
  text-align: right;
  font-weight: bold;
  color: #909399;
}

.legend {
  display: flex;
  gap: 24px;
  margin-top: 24px;
  padding: 12px;
  background: #f9fafc;
  border-radius: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #606266;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 6px;
}

.trend-note {
  margin-top: 16px;
}

/* 趋势分析卡片 */
.trend-analysis-card {
  height: 500px;
}

.trend-chart {
  height: 400px;
  width: 100%;
}

/* ✅ 移动端响应式优化 */
@media (max-width: 768px) {
  .cost-pie-chart {
    height: 200px;
  }
  .structure-chart {
    height: 300px;
  }
  .trend-chart {
    height: 250px;
  }
  .kpi-value {
    font-size: 20px;
  }
  .kpi-icon {
    width: 50px;
    height: 50px;
  }
}
</style>
