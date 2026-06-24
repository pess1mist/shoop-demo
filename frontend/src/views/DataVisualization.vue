<template>
  <div class="dashboard-container">
    <!-- 顶部品牌栏 -->
    <div class="header-bar">
      <div class="brand">🏭 智能制造内控管理系统</div>
      <div class="user-info">
        <el-avatar :size="32" style="margin-right: 8px">用户</el-avatar>
        <span>管理员</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-space>
        <el-tag type="success" size="large" effect="dark">
          <el-icon><TrendCharts /></el-icon>
          实时累计数据
        </el-tag>
        <span style="margin-left: 16px">产品线：</span>
        <el-select v-model="productLine" placeholder="请选择" style="width: 150px" @change="loadAllData">
          <el-option label="全公司" value="all" />
          <el-option label="膨化线" value="line1" />
          <el-option label="乳化线" value="line2" />
          <el-option label="水胶线" value="line3" />
        </el-select>
        <el-button type="primary" size="small" @click="loadAllData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </el-space>
    </div>

    <!-- 核心指标卡片 -->
    <el-row :gutter="16" class="kpi-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-label">总成本</div>
          <div class="kpi-value">{{ kpiData.totalCost || '加载中...' }}</div>
          <div class="kpi-footer">
            <span>累计至今</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-label">利润</div>
          <div class="kpi-value">{{ kpiData.profit || '加载中...' }}</div>
          <div class="kpi-footer">
            <span>累计至今</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-label">预算执行率</div>
          <div class="kpi-value">{{ kpiData.budgetExecutionRate || '加载中...' }}</div>
          <div class="kpi-footer">
            <el-progress :percentage="parseFloat(kpiData.budgetExecutionRate) || 78.5" :stroke-width="8" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-label">未处理预警</div>
          <div class="kpi-value">{{ kpiData.unhandledAlerts }}条</div>
          <div class="kpi-footer">
            <span style="color: #F56C6C">● 待处理</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区：三列布局 -->
    <el-row :gutter="16" class="main-content">
      <!-- 左侧：预警列表 -->
      <el-col :span="6">
        <el-card shadow="hover" class="alert-card">
          <template #header>
            <div class="card-header">
              <span>⚠️ 实时预警</span>
              <el-tag type="danger" size="small">处理中</el-tag>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(alert, index) in alerts"
              :key="index"
              :timestamp="alert.time"
              placement="top"
              :color="alert.color"
            >
              <el-card shadow="hover">
                <div class="alert-item">
                  <div class="alert-title">{{ alert.title }}</div>
                  <div class="alert-desc">{{ alert.desc }}</div>
                  <div class="alert-footer">
                    <el-tag :type="alert.statusType" size="small">{{ alert.status }}</el-tag>
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <!-- 中间：成本构成 -->
      <el-col :span="10">
        <el-card shadow="hover" class="cost-card">
          <template #header>
            <div class="card-header">
              <span>💰 成本构成</span>
              <el-tag type="success" size="small">累计至今</el-tag>
            </div>
          </template>
          <!-- ✅ 环形图展示成本构成 -->
          <div ref="costPieChartRef" class="cost-pie-chart"></div>

          <!-- 预算执行偏差 TOP3 -->
          <div class="variance-section">
            <div class="section-title">预算执行偏差 TOP3</div>
            <div v-for="(item, index) in budgetVarianceTop3" :key="index" class="variance-item">
              <span>{{ item.item }}</span>
              <el-tag :type="item.amount > 0 ? 'danger' : 'success'" size="small">
                {{ item.amount > 0 ? '+' : '' }}{{ item.amount }} 万
              </el-tag>
            </div>
            <div v-if="budgetVarianceTop3.length === 0" class="variance-item">
              <span>暂无数据</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：趋势图表 -->
      <el-col :span="8">
        <el-card shadow="hover" class="trend-card">
          <template #header>
            <div class="card-header">
              <span>📈 各产线经营对比</span>
              <el-tag type="info" size="small">收入/成本/利润</el-tag>
            </div>
          </template>
          <div ref="trendChartRef" class="trend-chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { Refresh, TrendCharts } from '@element-plus/icons-vue'
import {
  getGraduationDataOverview,
  getGraduationInternalControlLogs,
  getDataVisualization
} from '@/api'

// 筛选条件
const productLine = ref('all')
const loading = ref(false)

// 预警数据
const alerts = ref([])

// 预算偏差TOP3数据
const budgetVarianceTop3 = ref([])

// KPI 数据
const kpiData = ref({
  totalCost: '',
  profit: '',
  budgetExecutionRate: '',
  unhandledAlerts: 0
})

// 成本构成数据
const costStructure = ref({
  materialCost: 0,
  laborCost: 0,
  overheadCost: 0,
  materialPercent: 0,
  laborPercent: 0,
  overheadPercent: 0,
  unitCosts: {},  // 单位成本数据（从后端获取）
  quantities: {}  // 产量数据（从后端获取）
})

// 图表引用
const trendChartRef = ref(null)
const costPieChartRef = ref(null)  // ✅ 成本构成环形图
let trendChart = null
let costPieChart = null  // ✅ 成本构成图表实例

// 自动刷新定时器
let refreshTimer = null
const AUTO_REFRESH_INTERVAL = 60000 // 60 秒自动刷新一次

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    // 调用后端 API 获取数据
    const response = await getDataVisualization({
      year: 'all',
      productLine: productLine.value
    })
    
    console.log('=== 原始响应数据 ===', response)
    
    // 响应拦截器已经返回了 data，所以直接检查 response
    if (response && (response.totalCost || response.costStructure)) {
      // 更新 KPI 数据
      kpiData.value = {
        totalCost: response.totalCost || response.data?.totalCost,
        profit: response.profit || response.data?.profit,
        budgetExecutionRate: response.budgetExecutionRate || response.data?.budgetExecutionRate,
        unhandledAlerts: response.unhandledAlerts || response.data?.unhandledAlerts || 0
      }
      
      // 更新成本构成数据
      if (response.costStructure || response.data?.costStructure) {
        const costData = response.costStructure || response.data.costStructure
        costStructure.value = {
          materialCost: costData.materialCost || 0,
          laborCost: costData.laborCost || 0,
          overheadCost: costData.overheadCost || 0,
          materialPercent: parseFloat(costData.materialPercent) || 0,
          laborPercent: parseFloat(costData.laborPercent) || 0,
          overheadPercent: parseFloat(costData.overheadPercent) || 0,
          unitCosts: costData.unitCosts || {},
          quantities: costData.quantities || {}
        }
        
        console.log('=== 成本构成详细数据 ===')
        console.log('unitCosts:', costStructure.value.unitCosts)
        console.log('quantities:', costStructure.value.quantities)
      }
      
      // 更新预算偏差 TOP3 数据
      if (response.budgetVarianceTop3 && response.budgetVarianceTop3.length > 0) {
        budgetVarianceTop3.value = response.budgetVarianceTop3
        console.log('预算偏差 TOP3 数据已更新:', budgetVarianceTop3.value)
      } else {
        // 没有数据时不显示模拟数据，保持空数组
        budgetVarianceTop3.value = []
        console.log('后端未返回预算偏差数据')
      }
      
      // 更新预警数据
      updateAlerts(response)
      
      console.log('KPI 数据已更新:', kpiData.value)
      console.log('成本构成数据已更新:', costStructure.value)
    }

    // 渲染趋势图表
    await nextTick()
    renderTrendChart()
    renderCostPieChart()  // ✅ 渲染成本构成环形图
    
    ElMessage.success('数据已更新')
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 页面加载时自动获取数据
onMounted(() => {
  loadAllData()
  // 设置自动刷新
  refreshTimer = setInterval(() => {
    loadAllData()
  }, AUTO_REFRESH_INTERVAL)
  // 添加窗口大小改变事件监听器
  window.addEventListener('resize', handleResize)
})

// ✅ 监听产品线变化，自动重新加载数据
watch(productLine, () => {
  console.log('产品线变化，重新加载数据...')
  loadAllData()
}, { deep: true })

// 更新预警数据
const updateAlerts = (data) => {
  // 根据实际数据更新预警列表
  const logs = data.alerts || []
  // 兼容多种状态码：PENDING、未处理、PROCESSING
  const unhandled = logs.filter(log => 
    log.handleStatus === 'PENDING' || 
    log.handleStatus === '未处理' ||
    log.handleStatus === 'PROCESSING'
  )
  
  alerts.value = unhandled.slice(0, 3).map(log => ({
    time: new Date(log.alertTime).toLocaleString('zh-CN'),
    title: log.alertType,
    desc: log.alertContent,
    color: log.handleStatus === 'PENDING' ? '#F56C6C' : '#E6A23C',
    status: log.handleStatus === 'PENDING' ? '待处理' : '处理中',
    statusType: log.handleStatus === 'PENDING' ? 'danger' : 'warning'
  }))
  
  console.log('预警数据已更新:', alerts.value.length, '条')
  console.log('原始预警数据:', logs)
}

// ✅ 渲染趋势图表 - 改为收入/成本/利润多维度对比
const renderTrendChart = () => {
  if (!trendChartRef.value) return
  
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  
  // 从后端获取的单位成本和产量数据
  const unitCosts = costStructure.value.unitCosts || {}
  const quantities = costStructure.value.quantities || {}
  
  // 将对象转换为数组
  let productLines = Object.keys(unitCosts)
  
  // 如果没有数据，使用默认数据
  if (productLines.length === 0) {
    productLines = ['膨化线', '乳化线', '水胶线']
  }
  
  // ✅ 计算每个产品线的收入、成本、利润
  // 假设单价：膨化线6000元/吨，乳化线7000元/吨，水胶线5000元/吨
  const priceMap = {
    '膨化线': 6000,
    '乳化线': 7000,
    '水胶线': 5000
  }
  
  const revenueData = []  // 收入
  const costData = []     // 成本
  const profitData = []   // 利润
  
  productLines.forEach(line => {
    const quantity = parseFloat(quantities[line]) || 0
    const unitCost = parseFloat(unitCosts[line]) || 0
    const unitPrice = priceMap[line] || 5000
    
    const revenue = (quantity * unitPrice / 10000).toFixed(2)  // 转换为万元
    const cost = (quantity * unitCost / 10000).toFixed(2)
    const profit = (revenue - cost).toFixed(2)
    
    revenueData.push(parseFloat(revenue))
    costData.push(parseFloat(cost))
    profitData.push(parseFloat(profit))
  })
  
  console.log('=== 多维度对比图表数据 ===')
  console.log('产品线:', productLines)
  console.log('收入:', revenueData)
  console.log('成本:', costData)
  console.log('利润:', profitData)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: function(params) {
        let result = params[0].name + '<br/>'
        params.forEach(item => {
          result += item.marker + item.seriesName + ': ' + item.value + '万<br/>'
        })
        return result
      }
    },
    legend: {
      data: ['收入', '成本', '利润'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: productLines,
      axisLabel: {
        interval: 0,
        rotate: 0
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
        name: '收入',
        type: 'bar',
        data: revenueData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 1, color: '#188df0' }
          ])
        },
        barWidth: '25%'
      },
      {
        name: '成本',
        type: 'bar',
        data: costData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#fbc2eb' },
            { offset: 1, color: '#a6c1ee' }
          ])
        },
        barWidth: '25%'
      },
      {
        name: '利润',
        type: 'bar',
        data: profitData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#84fab0' },
            { offset: 1, color: '#8fd3f4' }
          ])
        },
        barWidth: '25%'
      }
    ]
  }
  
  trendChart.setOption(option)
}

// ✅ 渲染成本构成环形图
const renderCostPieChart = () => {
  if (!costPieChartRef.value) return
  
  if (!costPieChart) {
    costPieChart = echarts.init(costPieChartRef.value)
  }
  
  // 计算总成本（用于中心显示）
  const totalCost = costStructure.value.materialCost + 
                    costStructure.value.laborCost + 
                    costStructure.value.overheadCost
  
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
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '成本构成',
        type: 'pie',
        radius: ['40%', '70%'],  // 环形图
        center: ['35%', '50%'],  // 位置调整，给右侧图例留空间
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
          fontSize: 12
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        labelLine: {
          show: true,
          length: 15,
          length2: 10
        },
        data: [
          {
            value: costStructure.value.materialCost,
            name: '材料费',
            itemStyle: { color: '#409EFF' }
          },
          {
            value: costStructure.value.laborCost,
            name: '人工费',
            itemStyle: { color: '#67C23A' }
          },
          {
            value: costStructure.value.overheadCost,
            name: '制造费',
            itemStyle: { color: '#E6A23C' }
          }
        ]
      }
    ],
    // ✅ 中心显示总成本
    graphic: [
      {
        type: 'text',
        left: '28%',
        top: '45%',
        style: {
          text: '总成本',
          textAlign: 'center',
          fill: '#909399',
          fontSize: 12
        }
      },
      {
        type: 'text',
        left: '28%',
        top: '52%',
        style: {
          text: totalCost.toFixed(2) + '万',
          textAlign: 'center',
          fill: '#303133',
          fontSize: 16,
          fontWeight: 'bold'
        }
      }
    ]
  }
  
  costPieChart.setOption(option)
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  trendChart?.resize()
  costPieChart?.resize()  // ✅ 环形图也响应窗口变化
}

// 生命周期 - 组件挂载时加载数据并启动自动刷新
// 窗口大小改变时重新渲染图表

// 清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 清除定时器
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
  // 销毁图表实例
  trendChart?.dispose()
  costPieChart?.dispose()  // ✅ 销毁成本构成图表
})
</script>

<style scoped>
.dashboard-container {
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

.brand {
  font-size: 18px;
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

/* 核心指标卡片 */
.kpi-cards {
  margin-bottom: 16px;
}

.kpi-card {
  height: 140px;
}

.kpi-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.kpi-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

/* 主要内容区 */
.main-content {
  margin-bottom: 16px;
}

/* 预警卡片 */
.alert-card {
  height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.alert-item {
  padding: 8px 0;
}

.alert-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.alert-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.alert-footer {
  margin-top: 4px;
}

/* 成本卡片 */
.cost-card {
  height: 500px;
}

/* ✅ 成本构成环形图样式 */
.cost-pie-chart {
  width: 100%;
  height: 300px;
  margin-bottom: 16px;
}

.cost-value {
  width: 50px;
  text-align: right;
  font-weight: bold;
  color: #303133;
}

.variance-section {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.section-title {
  font-weight: bold;
  margin-bottom: 12px;
  color: #303133;
}

.variance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
}

/* 趋势卡片 */
.trend-card {
  height: 500px;
}

.trend-chart {
  height: 400px;
  width: 100%;
}

/* 时间线样式优化 */
:deep(.el-timeline-item__node) {
  width: 12px;
  height: 12px;
}

:deep(.el-card__body) {
  padding: 12px;
}
</style>
