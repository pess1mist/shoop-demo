<template>
  <div class="workflow-tasks">
    <!-- 顶部标题 -->
    <div class="page-header">
      <h2>我的待办任务</h2>
    </div>
    
    <!-- ✅ KPI 统计卡片 -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="kpi-card" @click="filterByStatus('PENDING')">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon :size="32"><Clock /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">待审批</div>
            <div class="kpi-value">{{ stats.pending }}</div>
            <div class="kpi-unit">个任务</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="kpi-card" @click="filterByStatus('APPROVED')">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon :size="32"><CircleCheck /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">已批准</div>
            <div class="kpi-value">{{ stats.approved }}</div>
            <div class="kpi-unit">个任务</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="kpi-card" @click="filterByStatus('REJECTED')">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
            <el-icon :size="32"><CircleClose /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">已驳回</div>
            <div class="kpi-value">{{ stats.rejected }}</div>
            <div class="kpi-unit">个任务</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="kpi-card" @click="filterByStatus('COMPLETED')">
          <div class="kpi-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon :size="32"><Finished /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">已完成</div>
            <div class="kpi-value">{{ stats.completed }}</div>
            <div class="kpi-unit">个任务</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-space>
        <span>任务状态：</span>
        <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 150px">
          <el-option label="待审批" value="PENDING" />
          <el-option label="已批准" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="已完成" value="COMPLETED" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-space>
    </div>
    
    <!-- 任务列表 -->
    <el-table :data="paginatedTasks" style="width: 100%" border>
      <el-table-column prop="nodeName" label="任务名称" width="180" />
      <el-table-column prop="instanceId" label="流程实例 ID" width="120" />
      <el-table-column prop="assigneeName" label="处理人" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" effect="light">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.startTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="comment" label="审批意见" show-overflow-tooltip />
      <el-table-column label="操作" fixed="right" width="150">
        <template #default="scope">
          <el-button 
            v-if="scope.row.status === 'PENDING'"
            type="primary" 
            size="small"
            @click="handleTask(scope.row)">
            处理
          </el-button>
          <el-button 
            type="info" 
            size="small"
            @click="viewHistory(scope.row.instanceId)">
            查看历史
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadTasks"
      @current-change="loadTasks"
      style="margin-top: 20px; justify-content: flex-end;"
    />
    
    <!-- 审批历史对话框 -->
    <el-dialog
      v-model="historyDialogVisible"
      title="审批历史记录"
      width="800px"
    >
      <el-table :data="historyTasks" style="width: 100%" border>
        <el-table-column prop="nodeName" label="审批节点" width="150" />
        <el-table-column prop="assigneeName" label="审批人" width="120" />
        <el-table-column prop="action" label="操作" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.action === 'APPROVE' ? 'success' : 'danger'" effect="light">
              {{ scope.row.action === 'APPROVE' ? '通过' : scope.row.action === 'REJECT' ? '驳回' : scope.row.action || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="审批意见" show-overflow-tooltip />
        <el-table-column prop="endTime" label="审批时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.endTime) }}
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="historyDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { Clock, CircleCheck, CircleClose, Finished, Search, Refresh } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();

const queryForm = reactive({
  status: ''
});

const tasks = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 审批历史对话框
const historyDialogVisible = ref(false);
const historyTasks = ref([]);

// 统计数据
const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0,
  completed: 0
});

// 分页后的任务数据
const paginatedTasks = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return tasks.value.slice(start, end);
});

// ✅ 从 localStorage 获取用户信息并提取 userId
const getUserInfo = () => {
  try {
    const userInfoStr = localStorage.getItem('userInfo');
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr);
      return userInfo.id || userInfo.userId || userInfo.id;
    }
  } catch (error) {
    console.error('解析用户信息失败:', error);
  }
  return null;
};

const userId = getUserInfo();

// ✅ 监听路由参数变化，检测刷新标记
watch(
  () => route.query,
  (newQuery) => {
    if (newQuery.refresh === 'true') {
      // 清除 URL 中的 refresh 参数
      router.replace({ query: {} })
      // 刷新数据
      currentPage.value = 1
      loadTasks()
      ElMessage.success('数据已刷新')
    }
  }
  // 移除 immediate，避免在 loadTasks 定义前执行
)

// 查询按钮点击
const handleSearch = () => {
  currentPage.value = 1;
  loadTasks();
};

// 重置按钮点击
const handleReset = () => {
  queryForm.status = '';
  currentPage.value = 1;
  loadTasks();
};

// 点击KPI卡片筛选
const filterByStatus = (status) => {
  queryForm.status = status;
  currentPage.value = 1;
  loadTasks();
  ElMessage.success(`已筛选：${getStatusText(status)}`);
};

// ✅ 组件挂载时加载数据
onMounted(() => {
  loadTasks()
})
const loadTasks = async () => {
  if (!userId) {
    ElMessage.warning('未获取到当前用户信息，请重新登录');
    tasks.value = [];
    total.value = 0;
    return;
  }
  try {
    const url = queryForm.status 
      ? `/api/workflow/task/my/${userId}?status=${queryForm.status}`
      : `/api/workflow/task/my/${userId}`;
    
    const res = await axios.get(url);
    tasks.value = res.data.data || [];
    total.value = tasks.value.length;
    
    // 计算统计数据
    calculateStats();
  } catch (error) {
    ElMessage.error('加载任务失败：' + error.message);
  }
};

// 计算各状态任务数量
const calculateStats = () => {
  stats.pending = tasks.value.filter(t => t.status === 'PENDING').length;
  stats.approved = tasks.value.filter(t => t.status === 'APPROVED').length;
  stats.rejected = tasks.value.filter(t => t.status === 'REJECTED').length;
  stats.completed = tasks.value.filter(t => t.status === 'COMPLETED').length;
};

// 处理任务
const handleTask = (task) => {
  // 跳转到任务处理页面
  router.push(`/workflow/tasks/${task.id}?instanceId=${task.instanceId}`);
};

// 查看历史
const viewHistory = async (instanceId) => {
  try {
    // 过滤出该实例的所有历史任务（已审批的任务）
    const instanceTasks = tasks.value.filter(t => t.instanceId === instanceId);
    
    if (instanceTasks.length === 0) {
      ElMessage.warning('暂无历史记录');
      return;
    }
    
    historyTasks.value = instanceTasks;
    historyDialogVisible.value = true;
  } catch (error) {
    ElMessage.error('获取历史记录失败：' + error.message);
  }
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

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  const date = new Date(dateTime);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};
</script>

<style scoped>
.workflow-tasks {
  padding: 20px;
}

.page-header {
  margin-bottom: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
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

/* 筛选栏 */
.filter-bar {
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.demo-form-inline {
  margin-bottom: 20px;
}

/* ✅ 移动端响应式 */
@media (max-width: 768px) {
  .kpi-value {
    font-size: 20px;
  }
  .kpi-icon {
    width: 50px;
    height: 50px;
  }
}
</style>
