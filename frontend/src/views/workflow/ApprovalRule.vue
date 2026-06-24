<template>
  <div class="workflow-rule-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审批规则管理</span>
          <el-button type="primary" @click="handleCreate">新增规则</el-button>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" :model="queryParams" class="query-form">
        <el-form-item label="流程标识">
          <el-select v-model="queryParams.processKey" placeholder="请选择流程" clearable style="width: 200px">
            <el-option label="采购审批" value="PURCHASE_PLAN" />
            <el-option label="预算调整" value="BUDGET_ADJUST" />
            <el-option label="销售订单" value="SALES_ORDER" />
          </el-select>
        </el-form-item>
        <el-form-item label="规则类型">
          <el-select v-model="queryParams.ruleType" placeholder="请选择类型" clearable style="width: 150px">
            <el-option label="金额阈值" value="AMOUNT" />
            <el-option label="条件分支" value="CONDITION" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 规则列表 -->
      <el-table :data="ruleList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ruleName" label="规则名称" min-width="180" />
        <el-table-column prop="processKey" label="流程标识" width="130">
          <template #default="{ row }">
            <el-tag v-if="row.processKey === 'PURCHASE_PLAN'" type="primary">采购审批</el-tag>
            <el-tag v-else-if="row.processKey === 'BUDGET_ADJUST'" type="success">预算调整</el-tag>
            <el-tag v-else-if="row.processKey === 'SALES_ORDER'" type="warning">销售订单</el-tag>
            <span v-else>{{ row.processKey }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ruleType" label="规则类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.ruleType === 'AMOUNT'" type="info">金额阈值</el-tag>
            <el-tag v-else type="warning">条件分支</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="thresholdAmount" label="金额阈值" width="140">
          <template #default="{ row }">
            {{ row.thresholdAmount ? `¥${Number(row.thresholdAmount).toLocaleString()}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="targetNodeKey" label="目标节点" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ getNodeName(row.targetNodeKey) }}
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="ACTIVE"
              inactive-value="INACTIVE"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleTest(row)">测试</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="流程标识" prop="processKey">
          <el-select v-model="formData.processKey" placeholder="请选择流程" style="width: 100%">
            <el-option label="采购审批" value="PURCHASE_PLAN" />
            <el-option label="预算调整" value="BUDGET_ADJUST" />
            <el-option label="销售订单" value="SALES_ORDER" />
          </el-select>
        </el-form-item>
        <el-form-item label="规则类型" prop="ruleType">
          <el-select v-model="formData.ruleType" placeholder="请选择类型" style="width: 100%">
            <el-option label="金额阈值" value="AMOUNT" />
            <el-option label="条件分支" value="CONDITION" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额阈值" prop="thresholdAmount" v-if="formData.ruleType === 'AMOUNT'">
          <el-input-number
            v-model="formData.thresholdAmount"
            :min="0"
            :precision="2"
            :step="10000"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="目标节点" prop="targetNodeKey">
          <el-select v-model="formData.targetNodeKey" placeholder="请选择目标节点" style="width: 100%">
            <el-option label="部门经理审批" value="dept_approve" />
            <el-option label="财务经理审批" value="finance_approve" />
            <el-option label="总经理审批" value="gm_approve" />
            <el-option label="销售经理审批" value="sales_approve" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-input-number v-model="formData.priority" :min="0" :max="100" style="width: 100%" />
          <div class="form-tip">数字越大优先级越高</div>
        </el-form-item>
        <el-form-item label="规则描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入规则描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 测试对话框 -->
    <el-dialog v-model="testDialogVisible" title="规则测试" width="500px">
      <el-form label-width="120px">
        <el-form-item label="规则名称">
          <span>{{ testRuleData.ruleName }}</span>
        </el-form-item>
        <el-form-item label="金额阈值">
          <span>¥{{ Number(testRuleData.thresholdAmount).toLocaleString() }}</span>
        </el-form-item>
        <el-form-item label="测试金额">
          <el-input-number
            v-model="testAmount"
            :min="0"
            :precision="2"
            :step="10000"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="executeTest">执行测试</el-button>
        </el-form-item>
        <el-form-item label="测试结果" v-if="testResult !== null">
          <el-tag :type="testResult ? 'success' : 'danger'">
            {{ testResult ? '✓ 匹配成功' : '✗ 不匹配' }}
          </el-tag>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getActiveRules,
  getRulesByProcessKey,
  createRule,
  updateRule,
  deleteRule,
  enableRule,
  disableRule,
  testRule as apiTestRule
} from '@/api/workflow-rule'

const loading = ref(false)
const ruleList = ref([])
const queryParams = reactive({
  processKey: '',
  ruleType: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitLoading = ref(false)
const formData = reactive({
  id: null,
  ruleName: '',
  processKey: '',
  ruleType: 'AMOUNT',
  thresholdAmount: 0,
  targetNodeKey: '',
  priority: 1,
  description: ''
})

const formRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  processKey: [{ required: true, message: '请选择流程标识', trigger: 'change' }],
  ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
  targetNodeKey: [{ required: true, message: '请输入目标节点', trigger: 'blur' }]
}

const testDialogVisible = ref(false)
const testRuleData = ref({})
const testAmount = ref(0)
const testResult = ref(null)

// 加载规则列表
const loadRules = async () => {
  loading.value = true
  try {
    let response
    if (queryParams.processKey) {
      response = await getRulesByProcessKey(queryParams.processKey)
    } else {
      response = await getActiveRules()
    }
    console.log('加载规则列表响应:', response)
    ruleList.value = response || []
    console.log('ruleList.value:', ruleList.value)
  } catch (error) {
    console.error('加载规则列表失败:', error)
    ElMessage.error('加载规则列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  loadRules()
}

// 重置查询
const resetQuery = () => {
  queryParams.processKey = ''
  queryParams.ruleType = ''
  loadRules()
}

// 新增规则
const handleCreate = () => {
  dialogTitle.value = '新增审批规则'
  dialogVisible.value = true
}

// 编辑规则
const handleEdit = (row) => {
  dialogTitle.value = '编辑审批规则'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (formData.id) {
        await updateRule(formData.id, formData)
        ElMessage.success('更新成功')
      } else {
        await createRule(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadRules()
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: null,
    ruleName: '',
    processKey: '',
    ruleType: 'AMOUNT',
    thresholdAmount: 0,
    targetNodeKey: '',
    priority: 1,
    description: ''
  })
  formRef.value?.clearValidate()
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    if (row.status === 'ACTIVE') {
      await enableRule(row.id)
      ElMessage.success('已启用')
    } else {
      await disableRule(row.id)
      ElMessage.success('已停用')
    }
  } catch (error) {
    ElMessage.error('状态变更失败')
    row.status = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  }
}

// 删除规则
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该规则吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRule(row.id)
      ElMessage.success('删除成功')
      loadRules()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 打开测试对话框
const handleTest = (row) => {
  testRuleData.value = row
  testAmount.value = row.thresholdAmount || 0
  testResult.value = null
  testDialogVisible.value = true
}

// 执行测试
const executeTest = async () => {
  try {
    const response = await apiTestRule(testRuleData.value.id, testAmount.value)
    testResult.value = response.data
  } catch (error) {
    ElMessage.error('测试失败')
  }
}

// 节点名称映射
const getNodeName = (nodeKey) => {
  const nodeNames = {
    'dept_approve': '部门经理审批',
    'finance_approve': '财务经理审批',
    'gm_approve': '总经理审批',
    'sales_approve': '销售经理审批',
    'DEPT_APPROVE': '部门经理审批',
    'FINANCE_APPROVE': '财务经理审批',
    'GM_APPROVE': '总经理审批',
    'SALES_APPROVE': '销售经理审批'
  }
  return nodeNames[nodeKey] || nodeKey
}

onMounted(() => {
  loadRules()
})
</script>

<style scoped>
.workflow-rule-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 20px;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>
