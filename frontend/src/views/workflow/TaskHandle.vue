<template>
  <div class="task-handle">
    <el-page-header @back="goBack" :title="'返回'">
      <template #content>
        <span class="page-title">任务处理</span>
      </template>
    </el-page-header>
    
    <div style="margin-top: 20px;">
      <el-card class="box-card" v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>任务信息</span>
            <el-tag :type="getStatusType(task.status)" effect="light" size="large">
              {{ getStatusText(task.status) }}
            </el-tag>
          </div>
        </template>
        
        <!-- 流程基本信息 -->
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务 ID">{{ task.id }}</el-descriptions-item>
          <el-descriptions-item label="任务名称">{{ task.taskName }}</el-descriptions-item>
          <el-descriptions-item label="流程实例 ID">{{ task.instanceId }}</el-descriptions-item>
          <el-descriptions-item label="业务单号">{{ instanceInfo.businessKey }}</el-descriptions-item>
          <el-descriptions-item label="发起人">{{ instanceInfo.startUserName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="发起时间">{{ formatTime(instanceInfo.startedTime) }}</el-descriptions-item>
          <el-descriptions-item label="处理人">{{ task.assigneeName }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(task.createdTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <el-divider content-position="left">业务详情</el-divider>
        
        <!-- 业务数据展示 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <template #header>
            <div class="business-header">
              <span>采购申请详情</span>
              <el-tag type="success">{{ instanceInfo.businessKey }}</el-tag>
            </div>
          </template>
          
          <el-descriptions :column="2" border>
            <el-descriptions-item label="申请人">{{ businessData.applicantName }}</el-descriptions-item>
            <el-descriptions-item label="申请部门">{{ businessData.applicantDept }}</el-descriptions-item>
            <el-descriptions-item label="申请日期">{{ businessData.applyDate }}</el-descriptions-item>
            <el-descriptions-item label="申请金额">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ Number(businessData.totalAmount).toLocaleString() }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="申请事由" :span="2">
              {{ businessData.reason }}
            </el-descriptions-item>
          </el-descriptions>
          
          <!-- 采购物品清单 -->
          <div style="margin-top: 20px;">
            <div style="font-weight: bold; margin-bottom: 10px;">采购物品清单：</div>
            <el-table :data="businessData.items" border size="small">
              <el-table-column prop="name" label="物品名称" width="200" />
              <el-table-column prop="quantity" label="数量" width="100" align="right" />
              <el-table-column prop="price" label="单价 (元)" width="120" align="right" />
              <el-table-column label="金额 (元)" align="right">
                <template #default="{ row }">
                  <span style="font-weight: bold;">¥{{ (row.quantity * row.price).toLocaleString() }}</span>
                </template>
              </el-table-column>
            </el-table>
            
            <div style="text-align: right; margin-top: 10px; font-size: 16px;">
              合计：<span style="color: #f56c6c; font-weight: bold;">¥{{ Number(businessData.totalAmount).toLocaleString() }}</span>
            </div>
          </div>
        </el-card>
        
        <el-divider content-position="left">审批操作</el-divider>
        
        <el-form :model="form" label-width="120px">
          <el-form-item label="审批意见" required>
            <el-input 
              v-model="form.opinion" 
              type="textarea"
              rows="6"
              placeholder="请输入审批意见，例如：同意采购/预算充足/需要进一步审核等"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="approve" :loading="submitting">通过</el-button>
            <el-button type="danger" @click="reject" :loading="submitting">驳回</el-button>
            <el-button @click="goBack">返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const submitting = ref(false);

const task = ref({
  id: 0,
  taskName: '',
  instanceId: 0,
  nodeId: 0,
  assigneeName: '',
  status: '',
  createdTime: '',
  opinion: ''
});

// 流程实例信息
const instanceInfo = ref({
  businessKey: '',
  businessType: '',
  startedBy: 0,
  startedTime: '',
  startUserName: ''
});

// 业务数据（采购申请等）
const businessData = ref({
  applicantName: '',
  applicantDept: '',
  applyDate: '',
  totalAmount: 0,
  reason: '',
  items: []
});

const form = reactive({
  opinion: ''
});

// 加载任务详情
onMounted(async () => {
  loading.value = true;
  try {
    const instanceId = route.query.instanceId;
    console.log('加载任务，instanceId:', instanceId);
    
    if (!instanceId) {
      ElMessage.error('缺少实例 ID 参数');
      loading.value = false;
      return;
    }
    
    // 1. 加载任务信息
    const taskRes = await axios.get(`/api/workflow/task/current/${instanceId}`);
    console.log('任务 API 返回:', taskRes.data);
    
    if (taskRes.data.code === 200 && taskRes.data.data) {
      task.value = taskRes.data.data;
      console.log('任务数据:', task.value);
    }
    
    // 2. 加载流程实例信息
    try {
      const instanceRes = await axios.get(`/api/workflow/instance/${instanceId}`);
      if (instanceRes.data.code === 200 && instanceRes.data.data) {
        instanceInfo.value = instanceRes.data.data;
        console.log('流程实例数据:', instanceInfo.value);
        
        // 3. 根据业务类型加载对应的业务数据
        await loadBusinessData(instanceRes.data.data.businessKey, instanceRes.data.data.businessType);
      }
    } catch (error) {
      console.warn('加载流程实例失败:', error.message);
      // 如果实例 API 不存在，使用默认值
      instanceInfo.value.startedTime = task.value.createdTime;
    }
    
    ElMessage.success('加载任务成功');
  } catch (error) {
    console.error('加载任务异常:', error);
    ElMessage.error('加载任务失败：' + (error.message || '网络错误'));
  } finally {
    loading.value = false;
  }
});

// 加载业务数据
const loadBusinessData = async (businessKey, businessType) => {
  try {
    console.log('加载业务数据，businessKey:', businessKey, 'businessType:', businessType);
    
    // 根据业务类型加载不同的业务数据
    if (businessType === 'PURCHASE') {
      // 加载采购申请数据
      try {
        const purchaseRes = await axios.get(`/api/purchase-plan/${businessKey}`);
        if (purchaseRes.data.code === 200 && purchaseRes.data.data) {
          const data = purchaseRes.data.data;
          businessData.value = {
            applicantName: data.createdByName || '未知',
            applicantDept: data.deptCode || '未知部门',
            applyDate: data.createdTime || '',
            totalAmount: data.totalAmount || 0,
            reason: data.remark || '',
            items: []
          };
          console.log('采购申请数据:', businessData.value);
        }
      } catch (error) {
        console.warn('加载采购申请数据失败:', error.message);
      }
    }
  } catch (error) {
    console.warn('加载业务数据失败:', error.message);
    // 清空业务数据
    businessData.value = {
      applicantName: '未知',
      applicantDept: '未知部门',
      applyDate: '',
      totalAmount: 0,
      reason: '',
      items: []
    };
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未创建';
  return time.replace('T', ' ');
};

// 通过
const approve = async () => {
  try {
    await ElMessageBox.confirm('确认通过该任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // ✅ 从 localStorage 获取用户信息并提取 userId 和 userName
    const getUserInfo = () => {
      try {
        const userInfoStr = localStorage.getItem('userInfo');
        if (userInfoStr) {
          const userInfo = JSON.parse(userInfoStr);
          return {
            id: userInfo.id || userInfo.userId,
            name: userInfo.username || userInfo.userName || userInfo.name
          };
        }
      } catch (error) {
        console.error('解析用户信息失败:', error);
      }
      return { id: null, name: null };
    };
    
    const { id: userId, name: userName } = getUserInfo();
    if (!userId || !userName) {
      ElMessage.warning('未获取到当前用户信息，请重新登录');
      return;
    }

    submitting.value = true;
    await axios.put('/api/workflow/task/complete', null, {
      params: {
        taskId: task.value.id,
        action: 'APPROVE',
        opinion: form.opinion || '同意',
        userId,
        userName
      }
    });
    
    ElMessage.success('审批通过！采购计划状态已自动更新');
    
    // 返回待办列表，并携带刷新标记
    router.push({ path: '/workflow/tasks', query: { refresh: 'true' } });
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('处理失败：' + (error.message || '网络错误'));
    }
  } finally {
    submitting.value = false;
  }
};

// 驳回
const reject = async () => {
  try {
    await ElMessageBox.confirm('确认驳回该任务吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    });
    
    // ✅ 从 localStorage 获取用户信息并提取 userId 和 userName
    const getUserInfo = () => {
      try {
        const userInfoStr = localStorage.getItem('userInfo');
        if (userInfoStr) {
          const userInfo = JSON.parse(userInfoStr);
          return {
            id: userInfo.id || userInfo.userId,
            name: userInfo.username || userInfo.userName || userInfo.name
          };
        }
      } catch (error) {
        console.error('解析用户信息失败:', error);
      }
      return { id: null, name: null };
    };
    
    const { id: userId, name: userName } = getUserInfo();
    if (!userId || !userName) {
      ElMessage.warning('未获取到当前用户信息，请重新登录');
      return;
    }

    submitting.value = true;
    await axios.put('/api/workflow/task/complete', null, {
      params: {
        taskId: route.params.id,
        action: 'REJECT',
        opinion: form.opinion || '不同意',
        userId,
        userName
      }
    });
    
    ElMessage.success('已驳回！采购计划状态已自动更新');
    
    // 返回待办列表，并携带刷新标记
    router.push({ path: '/workflow/tasks', query: { refresh: 'true' } });
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('处理失败：' + (error.message || '网络错误'));
    }
  } finally {
    submitting.value = false;
  }
};

// 返回
const goBack = () => {
  router.back();
};

// 状态类型映射
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'COMPLETED': 'success',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  };
  return types[status] || 'info';
};

// 状态文本映射
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待审批',
    'COMPLETED': '已完成',
    'APPROVED': '已批准',
    'REJECTED': '已驳回'
  };
  return texts[status] || status;
};
</script>

<style scoped>
.task-handle {
  padding: 20px;
}

.box-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.business-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
}
</style>
