<template>
  <div class="internal-control-container">
    <!-- 顶部品牌栏 -->
    <div class="header-bar">
      <div class="breadcrumb">
        <span>🏭 智能制造内控管理系统</span>
        <span style="margin: 0 8px">></span>
        <span>内控预警</span>
      </div>
      <div class="user-info">
        <el-avatar :size="32" style="margin-right: 8px">用户</el-avatar>
        <span>管理员</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-space>
        <span>预警级别：</span>
        <el-select v-model="alertLevel" placeholder="请选择" style="width: 120px" @change="loadData">
          <el-option label="全部级别" value="all" />
          <el-option label="高" value="high" />
          <el-option label="中" value="medium" />
          <el-option label="低" value="low" />
        </el-select>
        <span style="margin-left: 16px">处理状态：</span>
        <el-select v-model="handleStatus" placeholder="请选择" style="width: 120px" @change="loadData">
          <el-option label="全部状态" value="all" />
          <el-option label="未处理" value="unhandled" />
          <el-option label="处理中" value="processing" />
          <el-option label="已处理" value="handled" />
        </el-select>
        <span style="margin-left: 16px">时间：</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 240px"
          @change="loadData"
        />
        <el-button type="primary" size="small" @click="loadData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          查询
        </el-button>
      </el-space>
    </div>

    <!-- 核心指标 -->
    <el-row :gutter="16" class="kpi-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: #F56C6C">
            <el-icon :size="30"><Warning /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-label">总预警数</div>
            <div class="kpi-value">{{ totalAlerts }} 条</div>
            <div class="kpi-footer">本月累计</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: #F56C6C">
            <el-icon :size="30"><CircleClose /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-label">未处理</div>
            <div class="kpi-value">{{ unhandledAlerts }} 条</div>
            <div class="kpi-footer">需优先处理</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: #E6A23C">
            <el-icon :size="30"><Loading /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-label">处理中</div>
            <div class="kpi-value">{{ processingAlerts }} 条</div>
            <div class="kpi-footer">正在跟进</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: #67C23A">
            <el-icon :size="30"><CircleCheck /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-label">已处理</div>
            <div class="kpi-value">{{ handledAlerts }} 条</div>
            <div class="kpi-footer">处理率 {{ totalAlerts > 0 ? Math.round(handledAlerts / totalAlerts * 100) : 0 }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区 -->
    <el-row :gutter="16" class="main-content">
      <!-- 左侧：预警列表 -->
      <el-col :span="12">
        <el-card shadow="hover" class="alert-list-card">
          <template #header>
            <div class="card-header">
              <span>⚠️ 实时预警列表</span>
              <el-tag type="danger" size="small">未处理优先</el-tag>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(alert, index) in alertList"
              :key="index"
              :timestamp="alert.time"
              placement="top"
              :color="alert.color"
              :size="alert.level === '高' ? 'large' : 'normal'"
            >
              <el-card shadow="hover" class="alert-item-card">
                <div class="alert-item">
                  <div class="alert-header">
                    <span class="alert-title">{{ alert.title }}</span>
                    <el-tag :type="alert.levelType" size="small">{{ alert.level }}风险</el-tag>
                  </div>
                  <div class="alert-desc">{{ alert.desc }}</div>
                  <div class="alert-footer">
                    <div class="alert-meta">
                      <span>部门：{{ alert.department }}</span>
                      <span>责任人：{{ alert.responsible }}</span>
                    </div>
                    <div class="alert-actions">
                      <el-tag :type="alert.statusType" size="small">{{ alert.status }}</el-tag>
                      <el-button 
                        v-if="alert.status === '未处理'" 
                        type="primary" 
                        size="small" 
                        @click="handleAlert(alert)"
                      >
                        处理
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <!-- 右侧：预警类型分布 -->
      <el-col :span="12">
        <el-card shadow="hover" class="alert-type-card">
          <template #header>
            <div class="card-header">
              <span>📊 预警类型分布</span>
              <el-tag type="warning" size="small">风险分布</el-tag>
            </div>
          </template>
          <div ref="alertTypeChartRef" class="chart-container"></div>
          
          <!-- 风险等级统计 -->
          <div class="risk-stats">
            <div class="stat-item">
              <div class="stat-label">高风险</div>
              <div class="stat-value" style="color: #F56C6C">{{ highRiskCount }}</div>
              <div class="stat-bar">
                <div class="stat-progress" :style="{ width: totalAlerts > 0 ? (highRiskCount / totalAlerts * 100) + '%' : '0%', background: '#F56C6C' }"></div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">中风险</div>
              <div class="stat-value" style="color: #E6A23C">{{ mediumRiskCount }}</div>
              <div class="stat-bar">
                <div class="stat-progress" :style="{ width: totalAlerts > 0 ? (mediumRiskCount / totalAlerts * 100) + '%' : '0%', background: '#E6A23C' }"></div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-label">低风险</div>
              <div class="stat-value" style="color: #67C23A">{{ lowRiskCount }}</div>
              <div class="stat-bar">
                <div class="stat-progress" :style="{ width: totalAlerts > 0 ? (lowRiskCount / totalAlerts * 100) + '%' : '0%', background: '#67C23A' }"></div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：预警处理记录 -->
    <el-row :gutter="16" class="record-row">
      <el-col :span="24">
        <el-card shadow="hover" class="record-card">
          <template #header>
            <div class="card-header">
              <span>📋 预警处理记录</span>
              <el-tag type="info" size="small">最近 10 条</el-tag>
            </div>
          </template>
          <el-table :data="handleRecordData" stripe>
            <el-table-column prop="alertType" label="预警类型" width="150" />
            <el-table-column prop="level" label="风险等级" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.levelType" size="small">{{ row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="department" label="部门" width="120" />
            <el-table-column prop="description" label="预警描述" min-width="200" />
            <el-table-column prop="responsible" label="责任人" width="100" />
            <el-table-column prop="handleTime" label="处理时间" width="160" />
            <el-table-column prop="handleResult" label="处理结果" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.resultType" size="small">{{ row.handleResult }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { 
  Warning, 
  CircleClose, 
  Loading, 
  CircleCheck,
  Refresh 
} from '@element-plus/icons-vue'
import { getAlerts, updateInternalControlStatus } from '@/api'

// 筛选条件
const alertLevel = ref('all')
const handleStatus = ref('all')
const dateRange = ref(['2025-03-01', '2025-03-24'])
const loading = ref(false)

// 预警列表数据
const alertList = ref([])

// 预警处理记录数据
const handleRecordData = ref([])

// KPI 指标
const totalAlerts = ref(0)
const unhandledAlerts = ref(0)
const processingAlerts = ref(0)
const handledAlerts = ref(0)

// 风险等级计数
const highRiskCount = ref(0)
const mediumRiskCount = ref(0)
const lowRiskCount = ref(0)

// 图表引用
const alertTypeChartRef = ref(null)
let alertTypeChart = null

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 调用后端 API 获取预警数据
    const response = await getAlerts({
      status: handleStatus.value !== 'all' ? handleStatus.value : null
    })
    const data = response?.data ?? response
    
    if (data) {
      console.log('预警数据:', data)
      
      // 处理返回的数据并绑定到预警列表
      if (Array.isArray(data)) {
        alertList.value = data.map(log => ({
          id: log.logId,
          title: log.alertType,
          level: getRiskLevel(log.alertType),
          levelType: getLevelType(log.alertType),
          time: log.alertTime ? new Date(log.alertTime).toLocaleString('zh-CN') : '未知',
          color: getColor(log.alertType),
          desc: log.alertContent,
          department: getDepartment(log.alertType),
          responsible: log.handler || '待分配',
          status: log.handleStatus === 'handled' ? '已处理' : log.handleStatus === 'processing' ? '处理中' : '未处理',
          statusType: log.handleStatus === 'handled' ? 'success' : log.handleStatus === 'processing' ? 'warning' : 'danger'
        }))
        
        // 统计 KPI 数据
        totalAlerts.value = data.length
        unhandledAlerts.value = data.filter(log => log.handleStatus === 'unhandled').length
        processingAlerts.value = data.filter(log => log.handleStatus === 'processing').length
        handledAlerts.value = data.filter(log => log.handleStatus === 'handled').length
        
        // 统计风险等级
        highRiskCount.value = data.filter(log => getRiskLevel(log.alertType) === '高').length
        mediumRiskCount.value = data.filter(log => getRiskLevel(log.alertType) === '中').length
        lowRiskCount.value = data.filter(log => getRiskLevel(log.alertType) === '低').length
        
        console.log('预警列表数据已更新:', alertList.value.length, '条')
        console.log('KPI 统计:', { total: totalAlerts.value, unhandled: unhandledAlerts.value, processing: processingAlerts.value, handled: handledAlerts.value })
      }
    }
    
    await nextTick()
    renderAlertTypeChart()
    ElMessage.success('数据已更新')
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取风险等级
const getRiskLevel = (alertType) => {
  const levelMap = {
    '预算超支': '高',
    '成本异常': '高',
    '价格异常': '中',
    '库存预警': '中',
    '质量异常': '低'
  }
  return levelMap[alertType] || '中'
}

// 获取等级类型
const getLevelType = (alertType) => {
  const typeMap = {
    '预算超支': 'danger',
    '成本异常': 'danger',
    '价格异常': 'warning',
    '库存预警': 'warning',
    '质量异常': 'info'
  }
  return typeMap[alertType] || 'info'
}

// 获取颜色
const getColor = (alertType) => {
  const colorMap = {
    '预算超支': '#F56C6C',
    '成本异常': '#F56C6C',
    '价格异常': '#E6A23C',
    '库存预警': '#E6A23C',
    '质量异常': '#909399'
  }
  return colorMap[alertType] || '#909399'
}

// 获取部门
const getDepartment = (alertType) => {
  const deptMap = {
    '预算超支': '采购部',
    '成本异常': '生产部',
    '价格异常': '采购部',
    '库存预警': '仓储部',
    '质量异常': '质检部'
  }
  return deptMap[alertType] || '相关部门'
}

// 页面加载时获取数据
onMounted(() => {
  loadData()
})

// ✅ 监听筛选条件变化，自动重新加载数据
watch([alertLevel, handleStatus, dateRange], () => {
  console.log('筛选条件变化，重新加载内控预警数据...')
  loadData()
}, { deep: true })

// 处理预警
const handleAlert = async (alert) => {
  try {
    // 调用后端 API 更新状态
    const response = await updateInternalControlStatus(
      alert.id,
      'PROCESSING',
      '当前用户'
    )
    
    ElMessage.success('已开始处理预警')
    // 刷新列表
    await loadData()
  } catch (error) {
    ElMessage.error('处理失败：' + error.message)
  }
}

// 渲染预警类型分布图 - 从后端获取真实统计数据
const renderAlertTypeChart = async () => {
  if (!alertTypeChartRef.value) return
  
  if (!alertTypeChart) {
    alertTypeChart = echarts.init(alertTypeChartRef.value)
  }
  
  try {
    // 从后端获取预警统计数据
    const response = await getAlerts({})
    const data = response?.data ?? response
    
    let chartData = []
    
    if (Array.isArray(data)) {
      // 统计预警类型
      const typeCount = {}
      data.forEach(alert => {
        const type = alert.alertType
        // 数据库已改为中文，直接使用
        const chineseType = type || '未知类型'
        
        typeCount[chineseType] = (typeCount[chineseType] || 0) + 1
      })
      
      // 转换为图表数据
      chartData = Object.entries(typeCount).map(([name, value]) => ({
        value,
        name,
        itemStyle: {
          color: name === '超预算' || name === '成本超支' ? '#F56C6C' :
                 name === '价格异常' || name === '库存预警' ? '#E6A23C' :
                 '#909399'
        }
      }))
    }
    
    // 如果没有数据，使用默认提示
    if (chartData.length === 0) {
      chartData = [{ value: 0, name: '暂无数据', itemStyle: { color: '#D3DCE6' } }]
    }
    
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '预警类型',
          type: 'pie',
          radius: '60%',
          data: chartData,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          label: {
            formatter: '{b}: {c}条'
          }
        }
      ]
    }
    
    alertTypeChart.setOption(option)
  } catch (error) {
    console.error('加载预警统计数据失败:', error)
  }
}

// 生命周期
onMounted(() => {
  loadData()
  // 添加窗口大小改变事件监听器
  window.addEventListener('resize', handleResize)
})

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  alertTypeChart?.resize()
}

// 清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例
  alertTypeChart?.dispose()
})
</script>

<style scoped>
.internal-control-container {
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

/* 核心指标卡片 */
.kpi-cards {
  margin-bottom: 16px;
}

.kpi-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 12px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.kpi-card:hover {
  transform: translateY(-4px);
}

.kpi-icon {
  width: 52px;
  height: 52px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 12px;
}

.kpi-icon :deep(.el-icon) {
  font-size: 26px;
}

.kpi-content {
  width: 100%;
}

.kpi-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
  margin-bottom: 6px;
}

.kpi-footer {
  font-size: 11px;
  color: #909399;
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

.chart-container {
  height: 280px;
  width: 100%;
}

/* 预警列表卡片 */
.alert-list-card {
  height: 500px;
}

.alert-item-card {
  margin-bottom: 16px;
}

.alert-item {
  padding: 8px 0;
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.alert-title {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
}

.alert-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
  line-height: 1.5;
}

.alert-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.alert-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.alert-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 预警类型卡片 */
.alert-type-card {
  height: 500px;
}

.risk-stats {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.stat-label {
  width: 80px;
  font-size: 13px;
  color: #606266;
}

.stat-value {
  width: 40px;
  font-size: 18px;
  font-weight: bold;
  margin-right: 12px;
}

.stat-bar {
  flex: 1;
  height: 12px;
  background: #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
}

.stat-progress {
  height: 100%;
  border-radius: 6px;
  transition: width 0.3s;
}

/* 处理记录卡片 */
.record-row {
  margin-bottom: 16px;
}

.record-card {
  min-height: 400px;
}

/* 时间线样式优化 */
:deep(.el-timeline-item__node) {
  width: 12px;
  height: 12px;
}

:deep(.el-timeline-item__node--large) {
  width: 16px;
  height: 16px;
}
</style>
