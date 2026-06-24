<template>
  <div class="budget-execution-container">
    <!-- 顶部品牌栏 -->
    <div class="header-bar">
      <div class="breadcrumb">
        <span>🏭 智能制造内控管理系统</span>
        <span style="margin: 0 8px">></span>
        <span>预算执行</span>
      </div>
      <div class="user-info">
        <el-avatar :size="32" style="margin-right: 8px">用户</el-avatar>
        <span>管理员</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-space>
        <span>预算项目：</span>
        <el-select v-model="selectedDepartment" placeholder="请选择" style="width: 150px" @change="loadData">
          <el-option label="所有项目" value="all" />
          <el-option label="材料费" value="材料费" />
          <el-option label="人工费" value="人工费" />
          <el-option label="制造费用" value="制造费用" />
        </el-select>
        <span style="margin-left: 16px">时间：</span>
        <el-date-picker
          v-model="monthRange"
          type="monthrange"
          range-separator="至"
          start-placeholder="开始月份"
          end-placeholder="结束月份"
          value-format="YYYY-MM"
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
          <div class="kpi-icon" style="background: #409EFF">
            <el-icon :size="30"><Money /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-label">预算总额</div>
            <div class="kpi-value">¥{{ budgetSummary.totalBudget }}万</div>
            <div class="kpi-footer">{{ currentYear }} 年预算</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: #67C23A">
            <el-icon :size="30"><Document /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-label">已执行</div>
            <div class="kpi-value">¥{{ budgetSummary.totalActual }}万</div>
            <div class="kpi-footer">执行率 {{ executionRate }}%</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: #E6A23C">
            <el-icon :size="30"><TrendCharts /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-label">剩余预算</div>
            <div class="kpi-value">¥{{ budgetSummary.remaining }}万</div>
            <div class="kpi-footer">可用 {{ remainingRate }}%</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="kpi-card">
          <div class="kpi-icon" style="background: #F56C6C">
            <el-icon :size="30"><Warning /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-label">超预算</div>
            <div class="kpi-value">¥{{ budgetSummary.overBudget }}万</div>
            <div class="kpi-footer">{{ overBudgetCount }}个项目超支</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要内容区 -->
    <el-row :gutter="16" class="main-content">
      <!-- 左侧：预算执行率对比 -->
      <el-col :span="12">
        <el-card shadow="hover" class="execution-card">
          <template #header>
            <div class="card-header">
              <span>📊 预算执行率对比</span>
              <el-tag type="success" size="small">预 vs 实</el-tag>
            </div>
          </template>
          <div ref="executionChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 右侧：预算执行率趋势 -->
      <el-col :span="12">
        <el-card shadow="hover" class="department-card">
          <template #header>
            <div class="card-header">
              <span>📈 预算执行率趋势</span>
              <el-tag type="warning" size="small">月度分析</el-tag>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：预算执行明细（可折叠） -->
    <el-row :gutter="16" class="detail-row">
      <el-col :span="24">
        <el-card shadow="hover" class="detail-card">
          <template #header>
            <div class="card-header">
              <span>📋 预算执行明细表</span>
              <el-tag type="info" size="small">TOP 10</el-tag>
            </div>
          </template>
          
          <!-- ✅ 可折叠标题栏 -->
          <div class="table-header" @click="budgetTableCollapsed = !budgetTableCollapsed" style="cursor: pointer; display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: #f5f7fa; border-radius: 4px; margin-bottom: 12px;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-icon :size="16" style="transition: transform 0.3s;" :style="{ transform: budgetTableCollapsed ? 'rotate(-90deg)' : 'rotate(0deg)' }">
                <ArrowDown />
              </el-icon>
              <span style="font-size: 14px; font-weight: 500;">点击展开/折叠</span>
            </div>
          </div>
          
          <!-- ✅ 折叠内容区 -->
          <el-collapse-transition>
            <div v-show="!budgetTableCollapsed">
              <el-table :data="budgetDetailData" stripe>
                <el-table-column prop="project" label="预算项目" min-width="200" />
                <el-table-column prop="period" label="期间" width="100" />
                <el-table-column prop="budget" label="预算金额 (万)" width="120" align="right">
                  <template #default="{ row }">
                    <span style="font-weight: bold">{{ row.budget }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="actual" label="执行金额 (万)" width="120" align="right">
                  <template #default="{ row }">
                    <span :style="{ color: row.variance > 0 ? '#F56C6C' : '#67C23A', fontWeight: 'bold' }">
                      {{ row.actual }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="variance" label="偏差 (万)" width="100" align="right">
                  <template #default="{ row }">
                    <el-tag :type="row.variance > 0 ? 'danger' : 'success'" size="small">
                      {{ row.variance > 0 ? '+' : '' }}{{ row.variance }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="rate" label="执行率" width="120" align="right">
                  <template #default="{ row }">
                    <el-progress 
                      :percentage="parseFloat(row.rate)" 
                      :status="parseFloat(row.rate) > 100 ? 'exception' : 'success'"
                      :stroke-width="12"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-collapse-transition>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { Money, Document, TrendCharts, Warning, Refresh, ArrowDown } from '@element-plus/icons-vue'
import { getBudgetExecutionData } from '@/api'

// 筛选条件
const selectedDepartment = ref('all')
const currentYear = 2025  // ✅ 使用 2025 年以匹配数据库数据
const monthRange = ref([`${currentYear}-01`, `${currentYear}-12`])
const loading = ref(false)

// 预算执行明细表数据
const budgetDetailData = ref([])

// ✅ 预算执行明细表折叠状态（默认收缩）
const budgetTableCollapsed = ref(true)

// 预算汇总数据
const budgetSummary = ref({
  totalBudget: '0.00',
  totalActual: '0.00',
  remaining: '0.00',
  overBudget: '0.00'
})

// 计算执行率和剩余率
const executionRate = ref('0.00')
const remainingRate = ref('0.00')
const overBudgetCount = ref(0)

// 图表引用
const executionChartRef = ref(null)
const trendChartRef = ref(null)  // 预算执行率趋势图
let executionChart = null
let trendChart = null  // 预算执行率趋势图

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 调用后端 API 获取数据
    const response = await getBudgetExecutionData({
      department: selectedDepartment.value,
      monthRange: monthRange.value ? monthRange.value.join(',') : `${currentYear}-01,${currentYear}-12`
    })
    const data = response?.data ?? response
    
    if (data) {
      console.log('预算执行数据:', data)
      
      // ✅ 处理返回的数据并绑定到表格
      if (data.executions && Array.isArray(data.executions)) {
        budgetDetailData.value = data.executions.map(execution => ({
          project: execution.budgetItem,
          period: execution.period,
          budget: (execution.budgetAmount / 10000).toFixed(2),
          actual: (execution.actualAmount / 10000).toFixed(2),
          variance: (execution.variance / 10000).toFixed(2),
          rate: ((execution.actualAmount / execution.budgetAmount) * 100).toFixed(2)
        }))
        console.log('预算执行表格数据已更新:', budgetDetailData.value.length, '条')
        
        // 计算汇总数据
        if (data.summary) {
          budgetSummary.value = {
            totalBudget: data.summary.totalBudget,
            totalActual: data.summary.totalActual,
            remaining: data.summary.remaining,
            overBudget: data.summary.overBudget
          }
          
          // 计算执行率
          const totalBudget = parseFloat(data.summary.totalBudget)
          const totalActual = parseFloat(data.summary.totalActual)
          executionRate.value = totalBudget > 0 ? ((totalActual / totalBudget) * 100).toFixed(2) : '0.00'
          remainingRate.value = totalBudget > 0 ? (((totalBudget - totalActual) / totalBudget) * 100).toFixed(2) : '0.00'
          
          // 统计超预算项目数
          overBudgetCount.value = data.executions.filter(e => e.variance > 0).length
        }
      }
    }
    
    await nextTick()
    renderExecutionChart()
    renderTrendChart()
    ElMessage.success('数据已更新')
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadData()
  // 添加窗口大小改变事件监听器
  window.addEventListener('resize', handleResize)
})

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  executionChart?.resize()
  trendChart?.resize()
}

// 清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例
  executionChart?.dispose()
  trendChart?.dispose()
})

// ✅ 监听筛选条件变化，自动重新加载数据
watch([selectedDepartment, monthRange], () => {
  console.log('筛选条件变化，重新加载预算执行数据...')
  loadData()
}, { deep: true })

// 渲染预算执行率对比图
const renderExecutionChart = () => {
  if (!executionChartRef.value) return
  
  if (!executionChart) {
    executionChart = echarts.init(executionChartRef.value)
  }
  
  // 从预算执行数据中提取项目和金额
  const projectMap = {}
  budgetDetailData.value.forEach(item => {
    if (!projectMap[item.project]) {
      projectMap[item.project] = { budget: 0, actual: 0 }
    }
    projectMap[item.project].budget += parseFloat(item.budget) || 0
    projectMap[item.project].actual += parseFloat(item.actual) || 0
  })
  
  const projects = Object.keys(projectMap)
  const budgetData = projects.map(project => projectMap[project].budget)
  const actualData = projects.map(project => projectMap[project].actual)
  
  // 如果没有数据，使用空态占位，避免展示误导性业务数字
  const defaultProjects = ['暂无数据']
  const defaultBudgetData = [0]
  const defaultActualData = [0]
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['预算', '实际']
    },
    xAxis: {
      type: 'category',
      data: projects.length > 0 ? projects : defaultProjects
    },
    yAxis: {
      type: 'value',
      name: '金额 (万)'
    },
    series: [
      {
        name: '预算',
        type: 'bar',
        data: budgetData.length > 0 ? budgetData : defaultBudgetData,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '实际',
        type: 'bar',
        data: actualData.length > 0 ? actualData : defaultActualData,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  
  executionChart.setOption(option, true)  // true = notMerge，完全替换旧配置
}

// 渲染预算执行率趋势图
const renderTrendChart = () => {
  if (!trendChartRef.value) return
  
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  
  // 按月份和项目分组数据
  const monthMap = {}
  const projectMap = {}
  
  budgetDetailData.value.forEach(item => {
    const month = item.period
    const project = item.project
    
    if (!monthMap[month]) {
      monthMap[month] = {}
    }
    
    if (!monthMap[month][project]) {
      monthMap[month][project] = { budget: 0, actual: 0 }
    }
    
    monthMap[month][project].budget += parseFloat(item.budget) || 0
    monthMap[month][project].actual += parseFloat(item.actual) || 0
    
    projectMap[project] = true
  })
  
  const months = Object.keys(monthMap).sort()
  const projects = Object.keys(projectMap)
  
  // 如果没有数据，使用空态占位
  if (months.length === 0) {
    const option = {
      title: {
        text: '暂无数据',
        left: 'center',
        top: 'center',
        textStyle: { color: '#909399', fontSize: 16 }
      }
    }
    trendChart.setOption(option)
    return
  }
  
  // 为每个项目生成一条线
  const colors = ['#409EFF', '#67C23A', '#E6A23C']
  const series = projects.map((project, index) => {
    const data = months.map(month => {
      const budget = monthMap[month][project]?.budget || 0
      const actual = monthMap[month][project]?.actual || 0
      return budget > 0 ? ((actual / budget) * 100).toFixed(1) : 0
    })
    
    return {
      name: project,
      type: 'line',
      data: data,
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { width: 2, color: colors[index % colors.length] },
      itemStyle: { color: colors[index % colors.length] },
      markLine: index === 0 ? {
        data: [{ yAxis: 100, lineStyle: { color: '#F56C6C', type: 'dashed' }, label: { formatter: '100% 预算线' } }],
        silent: true
      } : undefined
    }
  })
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        let result = params[0].name + '<br/>'
        params.forEach(item => {
          const rate = parseFloat(item.value)
          const color = rate > 100 ? '#F56C6C' : '#67C23A'
          const status = rate > 100 ? '超支' : '节约'
          result += `<span style="display:inline-block;margin-right:5px;border-radius:50%;width:10px;height:10px;background-color:${item.color};"></span>`
          result += `${item.seriesName}: <span style="color:${color};font-weight:bold">${item.value}%</span> (${status})<br/>`
        })
        return result
      }
    },
    legend: {
      data: projects,
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
      boundaryGap: false,
      data: months,
      name: '月份'
    },
    yAxis: {
      type: 'value',
      name: '执行率 (%)',
      min: 0,
      max: 130,
      axisLabel: {
        formatter: '{value}%'
      },
      splitLine: {
        lineStyle: { type: 'dashed', color: '#E0E0E0' }
      }
    },
    series: series
  }
  
  trendChart.setOption(option, true)  // true = notMerge，完全替换旧配置
}
</script>

<style scoped>
.budget-execution-container {
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
  height: 300px;
  width: 100%;
}

/* 执行率卡片 */
.execution-card {
  height: 380px;
}

/* 部门卡片 */
.department-card {
  height: 380px;
}

/* 明细卡片 */
.detail-row {
  margin-bottom: 16px;
}

.detail-card {
  min-height: 420px;
}
</style>
