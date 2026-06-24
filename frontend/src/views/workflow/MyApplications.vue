<template>
  <div class="my-applications-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Document /></el-icon>
        我的采购申请
      </h2>
      <el-button type="primary" @click="$router.push('/workflow/purchase-apply')">
        <el-icon><Plus /></el-icon>
        新建申请
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <el-card shadow="hover" class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="状态筛选">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 150px" @change="loadData">
            <el-option label="全部状态" value="" />
            <el-option label="草稿" value="DRAFT" />
            <el-option label="待审批" value="PENDING" />
            <el-option label="已批准" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
            <el-option label="采购中" value="PURCHASING" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划名称">
          <el-input v-model="filterForm.planName" placeholder="搜索计划名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetFilter">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="hover" class="table-card">
      <el-table 
        v-loading="loading" 
        :data="tableData" 
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="planCode" label="计划单号" width="140" fixed />
        <el-table-column prop="planName" label="计划名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="deptName" label="申请部门" width="100" />
        <el-table-column prop="materialName" label="物料名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="quantity" label="数量" width="80" align="right" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.unitPrice).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">
              ¥{{ Number(row.totalAmount).toLocaleString() }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-space>
              <el-button 
                v-if="row.status === 'DRAFT'" 
                type="primary" 
                size="small" 
                link
                @click="handleSubmit(row)"
              >
                提交审批
              </el-button>
              <el-button 
                v-if="row.status === 'DRAFT'" 
                type="warning" 
                size="small" 
                link
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button 
                type="info" 
                size="small" 
                link
                @click="handleViewDetail(row)"
              >
                查看详情
              </el-button>
              <el-button 
                v-if="row.status === 'DRAFT' || row.status === 'REJECTED'" 
                type="danger" 
                size="small" 
                link
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="申请详情" 
      width="900px"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基本信息标签页 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border v-if="currentRow">
            <el-descriptions-item label="计划单号">{{ currentRow.planCode }}</el-descriptions-item>
            <el-descriptions-item label="计划名称">{{ currentRow.planName }}</el-descriptions-item>
            <el-descriptions-item label="申请部门">{{ currentRow.deptName }}</el-descriptions-item>
            <el-descriptions-item label="需求日期">{{ currentRow.requireDate }}</el-descriptions-item>
            <el-descriptions-item label="物料名称">{{ currentRow.materialName }}</el-descriptions-item>
            <el-descriptions-item label="物料编码">{{ currentRow.materialCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="采购数量">{{ currentRow.quantity }}</el-descriptions-item>
            <el-descriptions-item label="单价">¥{{ Number(currentRow.unitPrice).toLocaleString() }}</el-descriptions-item>
            <el-descriptions-item label="总金额" :span="2">
              <span style="color: #f56c6c; font-weight: bold; font-size: 16px;">
                ¥{{ Number(currentRow.totalAmount).toLocaleString() }}
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="供应商名称">{{ currentRow.supplierName }}</el-descriptions-item>
            <el-descriptions-item label="供应商编码">{{ currentRow.supplierCode || '-' }}</el-descriptions-item>
            <el-descriptions-item label="预算科目">
              {{ getBudgetItemText(currentRow.budgetItem) }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(currentRow.status)" effect="light">
                {{ getStatusText(currentRow.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请人">{{ currentRow.createdByName }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatTime(currentRow.createdTime) }}</el-descriptions-item>
            <el-descriptions-item label="申请事由" :span="2">
              {{ currentRow.remark || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        
        <!-- 审批进度标签页 -->
        <el-tab-pane label="审批进度" name="progress">
          <div v-loading="progressLoading" class="progress-container">
            <div v-if="workflowProgress" class="progress-content">
              <!-- 当前状态 -->
              <el-alert 
                :title="getInstanceStatusText(workflowProgress.status)" 
                :type="getInstanceStatusType(workflowProgress.status)"
                :closable="false"
                show-icon
                style="margin-bottom: 20px;"
              />
              
              <!-- 当前节点信息 -->
              <div v-if="workflowProgress.currentNode" class="current-node-info">
                <h4>当前审批节点</h4>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="节点名称">
                    {{ workflowProgress.currentNode.nodeName }}
                  </el-descriptions-item>
                  <el-descriptions-item label="审批人">
                    {{ workflowProgress.currentNode.assigneeName || '待分配' }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
              
              <!-- 审批流程步骤 -->
              <div class="steps-container">
                <h4>审批流程</h4>
                <el-steps :active="getActiveStep()" direction="vertical" finish-status="success">
                  <el-step 
                    v-for="(node, index) in workflowProgress.nodes" 
                    :key="index"
                    :title="node.nodeName"
                    :description="getStepDescription(node)"
                    :status="getNodeStepStatus(node)"
                  />
                </el-steps>
              </div>
              
              <!-- 开始和结束时间 -->
              <el-descriptions :column="2" border style="margin-top: 20px;">
                <el-descriptions-item label="发起时间">
                  {{ formatTime(workflowProgress.startedTime) }}
                </el-descriptions-item>
                <el-descriptions-item label="完成时间">
                  {{ workflowProgress.endedTime ? formatTime(workflowProgress.endedTime) : '-' }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
            
            <el-empty v-else description="暂无审批进度信息" />
          </div>
        </el-tab-pane>
        
        <!-- 审批记录标签页 -->
        <el-tab-pane label="审批记录" name="history">
          <div v-loading="progressLoading" class="history-container">
            <el-table 
              v-if="workflowProgress && workflowProgress.nodes" 
              :data="workflowProgress.nodes" 
              stripe
              border
              style="width: 100%"
            >
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column prop="nodeName" label="审批节点" width="150" />
              <el-table-column prop="assigneeName" label="审批人" width="120">
                <template #default="{ row }">
                  {{ row.assigneeName || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="审批动作" width="100" align="center">
                <template #default="{ row }">
                  <el-tag v-if="row.action === 'APPROVE'" type="success" size="small">通过</el-tag>
                  <el-tag v-else-if="row.action === 'REJECT'" type="danger" size="small">驳回</el-tag>
                  <el-tag v-else-if="row.taskStatus === 'PENDING'" type="info" size="small">待审批</el-tag>
                  <el-tag v-else type="info" size="small">{{ row.action || '-' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getTaskStatusType(row.taskStatus)" size="small">
                    {{ getTaskStatusText(row.taskStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="comment" label="审批意见" min-width="200" show-overflow-tooltip>
                <template #default="{ row }">
                  {{ row.comment || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="接收时间" width="160">
                <template #default="{ row }">
                  {{ row.startTime ? formatTime(row.startTime) : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="完成时间" width="160">
                <template #default="{ row }">
                  {{ row.endTime ? formatTime(row.endTime) : '-' }}
                </template>
              </el-table-column>
            </el-table>
            
            <el-empty v-else description="暂无审批记录" />
          </div>
        </el-tab-pane>
      </el-tabs>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Plus, Search, RefreshLeft } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const detailDialogVisible = ref(false)
const currentRow = ref(null)
const activeTab = ref('basic')
const workflowProgress = ref(null)
const progressLoading = ref(false)

// 筛选表单
const filterForm = reactive({
  status: '',
  planName: ''
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 获取当前用户信息
const getCurrentUser = () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      return {
        id: userInfo.id || userInfo.userId,
        name: userInfo.username || userInfo.userName || userInfo.name
      }
    }
  } catch (error) {
    console.error('解析用户信息失败:', error)
  }
  return { id: null, name: null }
}

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    
    const user = getCurrentUser()
    if (!user.id) {
      ElMessage.warning('未获取到用户信息')
      return
    }

    // TODO: 后端需要添加按创建人查询的接口
    // 暂时使用分页查询所有，前端过滤
    const response = await axios.get('/api/purchase/plan/page', {
      params: {
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize
      }
    })

    if (response.data.code === 200) {
      const data = response.data.data
      
      // 前端过滤：只显示当前用户的申请
      let filteredList = data.records.filter(item => item.createdBy === user.id)
      
      // 状态过滤
      if (filterForm.status) {
        filteredList = filteredList.filter(item => item.status === filterForm.status)
      }
      
      // 名称过滤
      if (filterForm.planName) {
        filteredList = filteredList.filter(item => 
          item.planName.includes(filterForm.planName)
        )
      }
      
      tableData.value = filteredList
      pagination.total = filteredList.length
    } else {
      ElMessage.error(response.data.message || '加载失败')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 重置筛选
const resetFilter = () => {
  filterForm.status = ''
  filterForm.planName = ''
  pagination.pageNum = 1
  loadData()
}

// 提交审批
const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认提交"${row.planName}"进行审批？`,
      '提交确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const user = getCurrentUser()
    if (!user.id || !user.name) {
      ElMessage.error('未获取到用户信息')
      return
    }

    const response = await axios.put(`/api/purchase/plan/${row.id}/submit`, null, {
      params: {
        userId: user.id,
        userName: user.name
      }
    })

    if (response.data.code === 200) {
      ElMessage.success('提交成功！')
      loadData()
    } else {
      ElMessage.error(response.data.message || '提交失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
      ElMessage.error('提交失败：' + (error.response?.data?.message || error.message))
    }
  }
}

// 编辑（跳转到申请页面并携带数据）
const handleEdit = (row) => {
  // TODO: 实现编辑功能，需要传递数据到申请页面
  ElMessage.info('编辑功能开发中...')
}

// 查看详情
const handleViewDetail = async (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
  activeTab.value = 'basic'
  
  // 加载审批进度
  await loadWorkflowProgress(row.id)
}

// 加载审批进度
const loadWorkflowProgress = async (planId) => {
  try {
    progressLoading.value = true
    const response = await axios.get(`/api/purchase/plan/${planId}/workflow-progress`)
    
    if (response.data.code === 200) {
      workflowProgress.value = response.data.data
    } else {
      workflowProgress.value = null
    }
  } catch (error) {
    console.error('加载审批进度失败:', error)
    workflowProgress.value = null
  } finally {
    progressLoading.value = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除"${row.planName}"？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )

    const response = await axios.delete(`/api/purchase/plan/${row.id}`)

    if (response.data.code === 200) {
      ElMessage.success('删除成功！')
      loadData()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败：' + (error.response?.data?.message || error.message))
    }
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

// 获取预算科目文本
const getBudgetItemText = (item) => {
  const textMap = {
    'RAW_MATERIAL': '原材料采购',
    'EQUIPMENT_PARTS': '设备配件',
    'OFFICE_SUPPLIES': '办公用品',
    'MARKETING': '营销费用',
    'R_AND_D': '研发费用'
  }
  return textMap[item] || item
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'PURCHASING': 'primary',
    'COMPLETED': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'DRAFT': '草稿',
    'PENDING': '待审批',
    'APPROVED': '已批准',
    'REJECTED': '已驳回',
    'PURCHASING': '采购中',
    'COMPLETED': '已完成'
  }
  return textMap[status] || status
}

// 获取流程实例状态文本
const getInstanceStatusText = (status) => {
  const textMap = {
    'RUNNING': '审批进行中',
    'COMPLETED': '审批已完成',
    'TERMINATED': '审批已终止'
  }
  return textMap[status] || status
}

// 获取流程实例状态类型
const getInstanceStatusType = (status) => {
  const typeMap = {
    'RUNNING': 'info',
    'COMPLETED': 'success',
    'TERMINATED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取当前激活的步骤
const getActiveStep = () => {
  if (!workflowProgress.value || !workflowProgress.value.nodes) {
    return 0
  }
  
  const nodes = workflowProgress.value.nodes
  for (let i = 0; i < nodes.length; i++) {
    if (nodes[i].taskStatus === 'PENDING') {
      return i
    }
  }
  return nodes.length
}

// 获取步骤描述
const getStepDescription = (node) => {
  if (node.taskStatus === 'COMPLETED' || node.taskStatus === 'APPROVED') {
    const time = node.endTime ? formatTime(node.endTime) : ''
    return `${node.assigneeName || '系统'} - ${time}`
  } else if (node.taskStatus === 'REJECTED') {
    return `已驳回 - ${node.assigneeName || ''}`
  } else if (node.taskStatus === 'PENDING') {
    return node.assigneeName ? `待${node.assigneeName}审批` : '待处理'
  }
  return ''
}

// 获取节点步骤状态
const getNodeStepStatus = (node) => {
  if (node.taskStatus === 'COMPLETED' || node.taskStatus === 'APPROVED') {
    return 'success'
  } else if (node.taskStatus === 'REJECTED') {
    return 'error'
  } else if (node.taskStatus === 'PENDING') {
    return 'process'
  }
  return 'wait'
}

// 获取任务状态类型
const getTaskStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'COMPLETED': 'success',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'TERMINATED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取任务状态文本
const getTaskStatusText = (status) => {
  const textMap = {
    'PENDING': '待审批',
    'COMPLETED': '已完成',
    'APPROVED': '已通过',
    'REJECTED': '已驳回',
    'TERMINATED': '已终止'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadData()
  
  // 设置定时刷新（每30秒刷新一次数据）
  const refreshInterval = setInterval(() => {
    loadData()
  }, 30000)
  
  // 组件卸载时清除定时器
  return () => clearInterval(refreshInterval)
})
</script>

<style scoped>
.my-applications-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.page-title .el-icon {
  font-size: 24px;
  color: #409EFF;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  margin: 0;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 表格样式优化 */
:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table__row:hover) {
  background: #ecf5ff;
}

/* 状态标签 */
:deep(.el-tag) {
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .my-applications-container {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .filter-form {
    display: flex;
    flex-direction: column;
  }
  
  :deep(.el-form-item) {
    margin-bottom: 10px;
    width: 100%;
  }
  
  :deep(.el-form-item__content) {
    width: 100%;
  }
}

/* 审批进度样式 */
.progress-container {
  padding: 10px 0;
}

.progress-content {
  padding: 0 10px;
}

.current-node-info {
  margin-bottom: 20px;
}

.current-node-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.steps-container {
  margin-bottom: 20px;
}

.steps-container h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

:deep(.el-steps--vertical) {
  padding: 10px 0;
}

:deep(.el-step__title) {
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-step__description) {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

/* 审批记录样式 */
.history-container {
  padding: 10px 0;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}
</style>
