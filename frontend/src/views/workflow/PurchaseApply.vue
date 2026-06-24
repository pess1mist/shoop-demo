<template>
  <div class="purchase-apply-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <el-page-header @back="$router.back()" content="新建采购申请" />
    </div>

    <!-- 申请表单 -->
    <el-card shadow="hover" class="form-card">
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="140px"
        class="purchase-form"
      >
        <!-- 基本信息 -->
        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          基本信息
        </el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划名称" prop="planName">
              <el-input v-model="form.planName" placeholder="请输入采购计划名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="需求日期" prop="requireDate">
              <el-date-picker
                v-model="form.requireDate"
                type="date"
                placeholder="选择需求日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请部门" prop="deptCode">
              <el-select v-model="form.deptCode" placeholder="请选择部门" style="width: 100%" @change="handleDeptChange">
                <el-option label="生产部" value="PROD" />
                <el-option label="技术部" value="TECH" />
                <el-option label="市场部" value="MARKET" />
                <el-option label="行政部" value="ADMIN" />
                <el-option label="财务部" value="FINANCE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预算科目" prop="budgetItem">
              <el-select v-model="form.budgetItem" placeholder="请选择预算科目" style="width: 100%">
                <el-option label="原材料采购" value="RAW_MATERIAL" />
                <el-option label="设备配件" value="EQUIPMENT_PARTS" />
                <el-option label="办公用品" value="OFFICE_SUPPLIES" />
                <el-option label="营销费用" value="MARKETING" />
                <el-option label="研发费用" value="R_AND_D" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 物料信息 -->
        <el-divider content-position="left">
          <el-icon><Box /></el-icon>
          物料信息
        </el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="物料名称" prop="materialName">
              <el-input v-model="form.materialName" placeholder="请输入物料名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料编码" prop="materialCode">
              <el-input v-model="form.materialCode" placeholder="请输入物料编码（可选）" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="采购数量" prop="quantity">
              <el-input-number 
                v-model="form.quantity" 
                :min="1" 
                :precision="0"
                style="width: 100%"
                placeholder="请输入数量"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价（元）" prop="unitPrice">
              <el-input-number 
                v-model="form.unitPrice" 
                :min="0.01" 
                :precision="2"
                :step="100"
                style="width: 100%"
                placeholder="请输入单价"
                @change="calculateTotal"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总金额（元）">
              <el-input 
                :value="totalAmountDisplay" 
                disabled
                style="width: 100%"
              >
                <template #prefix>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 供应商信息 -->
        <el-divider content-position="left">
          <el-icon><Shop /></el-icon>
          供应商信息
        </el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商名称" prop="supplierName">
              <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商编码">
              <el-input v-model="form.supplierCode" placeholder="请输入供应商编码（可选）" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 备注说明 -->
        <el-divider content-position="left">
          <el-icon><ChatDotRound /></el-icon>
          备注说明
        </el-divider>

        <el-form-item label="申请事由" prop="remark">
          <el-input 
            v-model="form.remark" 
            type="textarea"
            :rows="4"
            placeholder="请详细说明采购原因、用途等信息"
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-space>
            <el-button type="primary" size="large" @click="submitForm" :loading="submitting">
              <el-icon><Promotion /></el-icon>
              提交审批
            </el-button>
            <el-button size="large" @click="saveDraft" :loading="saving">
              <el-icon><DocumentAdd /></el-icon>
              保存草稿
            </el-button>
            <el-button size="large" @click="resetForm">
              <el-icon><RefreshLeft /></el-icon>
              重置
            </el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 审批流程提示 -->
    <el-card shadow="never" class="workflow-tip">
      <template #header>
        <div class="tip-header">
          <el-icon><InfoFilled /></el-icon>
          <span>审批流程说明</span>
        </div>
      </template>
      <el-steps :active="0" finish-status="success" align-center>
        <el-step title="提交申请" description="申请人填写表单并提交" />
        <el-step title="部门经理审批" description="部门负责人审核" />
        <el-step title="财务审批" description="财务部门审核预算" />
        <el-step title="审批完成" description="生成采购订单" />
      </el-steps>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, Box, Shop, ChatDotRound, Promotion, 
  DocumentAdd, RefreshLeft, InfoFilled 
} from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const saving = ref(false)

// 表单数据
const form = reactive({
  planName: '',
  deptCode: '',
  deptName: '',
  materialName: '',
  materialCode: '',
  quantity: null,
  unitPrice: null,
  supplierName: '',
  supplierCode: '',
  budgetItem: '',
  requireDate: '',
  remark: ''
})

// 表单验证规则
const rules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  deptCode: [
    { required: true, message: '请选择申请部门', trigger: 'change' }
  ],
  materialName: [
    { required: true, message: '请输入物料名称', trigger: 'blur' }
  ],
  quantity: [
    { required: true, message: '请输入采购数量', trigger: 'blur' }
  ],
  unitPrice: [
    { required: true, message: '请输入单价', trigger: 'blur' }
  ],
  supplierName: [
    { required: true, message: '请输入供应商名称', trigger: 'blur' }
  ],
  budgetItem: [
    { required: true, message: '请选择预算科目', trigger: 'change' }
  ],
  requireDate: [
    { required: true, message: '请选择需求日期', trigger: 'change' }
  ],
  remark: [
    { required: true, message: '请填写申请事由', trigger: 'blur' },
    { min: 10, message: '申请事由至少10个字符', trigger: 'blur' }
  ]
}

// 计算总金额
const totalAmount = computed(() => {
  if (form.quantity && form.unitPrice) {
    return form.quantity * form.unitPrice
  }
  return 0
})

// 总金额显示格式化
const totalAmountDisplay = computed(() => {
  return totalAmount.value.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
})

// 计算总金额
const calculateTotal = () => {
  // 自动触发计算
}

// 部门变化处理
const handleDeptChange = (value) => {
  const deptMap = {
    'PROD': '生产部',
    'TECH': '技术部',
    'MARKET': '市场部',
    'ADMIN': '行政部',
    'FINANCE': '财务部'
  }
  form.deptName = deptMap[value] || ''
}

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

// 提交审批
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请完善表单信息')
      return
    }

    try {
      await ElMessageBox.confirm(
        `确认提交采购申请？\n\n计划名称：${form.planName}\n总金额：¥${totalAmountDisplay.value}`,
        '提交确认',
        {
          confirmButtonText: '确定提交',
          cancelButtonText: '再检查一下',
          type: 'info'
        }
      )

      submitting.value = true
      
      // 获取当前用户
      const user = getCurrentUser()
      if (!user.id || !user.name) {
        ElMessage.error('未获取到当前用户信息，请重新登录')
        return
      }

      // 构建请求数据
      const requestData = {
        ...form,
        totalAmount: totalAmount.value,
        createdBy: user.id,
        createdByName: user.name,
        status: 'PENDING'
      }

      // 调用后端API创建采购计划
      const response = await axios.post('/api/purchase/plan', requestData)
            
      if (response.data.code === 200 && response.data.data) {
        const planId = response.data.data.id || response.data.data
              
        // 提交审批
        await axios.put(`/api/purchase/plan/${planId}/submit`, null, {
          params: {
            userId: user.id,
            userName: user.name
          }
        })
        
        ElMessage.success('采购申请提交成功！')
        
        // 延迟跳转到我的申请列表
        setTimeout(() => {
          router.push('/workflow/my-applications')
        }, 1500)
      } else {
        ElMessage.error(response.data.message || '提交失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('提交失败:', error)
        ElMessage.error('提交失败：' + (error.response?.data?.message || error.message))
      }
    } finally {
      submitting.value = false
    }
  })
}

// 保存草稿
const saveDraft = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请完善必填信息')
      return
    }

    try {
      saving.value = true
      
      const user = getCurrentUser()
      if (!user.id || !user.name) {
        ElMessage.error('未获取到当前用户信息，请重新登录')
        return
      }

      const requestData = {
        ...form,
        totalAmount: totalAmount.value,
        createdBy: user.id,
        createdByName: user.name,
        status: 'DRAFT'
      }

      const response = await axios.post('/api/purchase/plan', requestData)
      
      if (response.data.code === 200) {
        ElMessage.success('草稿保存成功！')
        setTimeout(() => {
          router.push('/workflow/my-applications')
        }, 1500)
      } else {
        ElMessage.error(response.data.message || '保存失败')
      }
    } catch (error) {
      console.error('保存失败:', error)
      ElMessage.error('保存失败：' + (error.response?.data?.message || error.message))
    } finally {
      saving.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<style scoped>
.purchase-apply-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 20px;
}

.form-card {
  margin-bottom: 20px;
}

.purchase-form {
  max-width: 1200px;
  margin: 0 auto;
}

/* 分隔线样式优化 */
:deep(.el-divider__text) {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 表单项间距 */
:deep(.el-form-item) {
  margin-bottom: 22px;
}

/* 输入框聚焦效果 */
:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #409EFF inset;
}

/* 数字输入框样式 */
:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number .el-input__inner) {
  text-align: left;
}

/* 禁用输入框样式 */
:deep(.el-input.is-disabled .el-input__inner) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

/* 操作按钮区域 */
:deep(.el-form-item:last-child) {
  margin-top: 30px;
  margin-bottom: 0;
}

/* 审批流程提示卡片 */
.workflow-tip {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid #bae6fd;
}

.tip-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #0369a1;
}

.tip-header .el-icon {
  font-size: 18px;
}

/* 步骤条样式 */
:deep(.el-step__title) {
  font-size: 14px;
  font-weight: 600;
}

:deep(.el-step__description) {
  font-size: 12px;
  color: #64748b;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .purchase-apply-container {
    padding: 10px;
  }
  
  .purchase-form {
    max-width: 100%;
  }
  
  :deep(.el-form-item__label) {
    width: 100px !important;
  }
}
</style>
