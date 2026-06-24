<template>
  <div class="workflow-instance">
    <h2>流程实例历史</h2>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.total }}</div>
              <div class="stat-label">总流程数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon running">
              <el-icon :size="30"><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.running }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon :size="30"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon :size="30"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pending }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 筛选条件 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="流程名称">
          <el-select v-model="searchForm.processKey" placeholder="全部流程" clearable style="width: 180px">
            <el-option label="采购审批流程" value="PURCHASE_PLAN" />
            <el-option label="预算调整流程" value="BUDGET_ADJUST" />
            <el-option label="预警工单流程" value="ALERT_WORKORDER" />
            <el-option label="销售订单流程" value="SALES_ORDER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="进行中" value="RUNNING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="待审批" value="PENDING" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已终止" value="TERMINATED" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务单号">
          <el-input v-model="searchForm.businessKey" placeholder="请输入业务单号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 流程实例列表 -->
    <el-table :data="instances" style="width: 100%" border v-loading="loading">
      <el-table-column prop="id" label="实例 ID" width="100" />
      <el-table-column prop="processKey" label="流程标识" width="150">
        <template #default="scope">
          {{ getProcessKeyText(scope.row.processKey || scope.row.processName) }}
        </template>
      </el-table-column>
      <el-table-column prop="processName" label="流程名称" width="180">
        <template #default="scope">
          {{ getProcessName(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column prop="businessType" label="业务类型" width="120">
        <template #default="scope">
          {{ getBusinessTypeText(scope.row.businessType) }}
        </template>
      </el-table-column>
      <el-table-column prop="businessKey" label="业务单号" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" effect="light">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startedTime" label="开始时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.startedTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="endedTime" label="结束时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.endedTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="startedBy" label="发起人" width="100">
        <template #default="scope">
          {{ scope.row.initiatorName || scope.row.startedBy || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small"
            @click="viewDetail(scope.row)">
            查看
          </el-button>
          <el-button 
            v-if="scope.row.status === 'RUNNING' || scope.row.status === '进行中'"
            type="danger" 
            size="small"
            @click="terminateProcess(scope.row)">
            终止
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
      @size-change="loadInstances"
      @current-change="loadInstances"
      style="margin-top: 20px; justify-content: flex-end;"
    />
    
    <!-- 流程详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="流程实例详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentInstance" class="detail-container">
        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="2" border class="detail-section">
          <el-descriptions-item label="流程名称">
            {{ getProcessName(currentInstance) }}
          </el-descriptions-item>
          <el-descriptions-item label="流程标识">
            {{ currentInstance.processKey }}
          </el-descriptions-item>
          <el-descriptions-item label="业务类型">
            {{ getBusinessTypeText(currentInstance.businessType) }}
          </el-descriptions-item>
          <el-descriptions-item label="业务单号">
            {{ currentInstance.businessKey }}
          </el-descriptions-item>
          <el-descriptions-item label="发起人">
            {{ currentInstance.initiatorName || currentInstance.startedBy }}
          </el-descriptions-item>
          <el-descriptions-item label="发起时间">
            {{ formatDateTime(currentInstance.startedTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="当前节点">
            {{ currentInstance.currentNode || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentInstance.status)">
              {{ getStatusText(currentInstance.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 流程节点进度 -->
        <div class="detail-section">
          <h3 class="section-title">流程进度</h3>
          <el-timeline v-if="taskList.length > 0">
            <el-timeline-item
              v-for="(task, index) in taskList"
              :key="index"
              :timestamp="formatDateTime(task.endTime || task.startTime)"
              placement="top"
              :type="getTaskStatusType(task.status)"
            >
              <el-card>
                <h4>{{ task.nodeName }}</h4>
                <p>处理人：{{ task.assigneeName || '-' }}</p>
                <p>状态：
                  <el-tag :type="getTaskStatusType(task.status)" size="small">
                    {{ getTaskStatusText(task.status) }}
                  </el-tag>
                </p>
                <p v-if="task.action">操作：{{ getActionText(task.action) }}</p>
                <p v-if="task.comment">意见：{{ task.comment }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无节点信息" />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Document,
  Loading,
  CircleCheck,
  Clock,
  Search,
  Refresh
} from '@element-plus/icons-vue';

const instances = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const detailVisible = ref(false);
const currentInstance = ref(null);
const taskList = ref([]);

// 统计数据
const statistics = reactive({
  total: 0,
  running: 0,
  completed: 0,
  pending: 0
});

// 搜索表单
const searchForm = reactive({
  processKey: '',
  status: '',
  businessKey: '',
  dateRange: [],
  startTime: '',
  endTime: ''
});

// 加载流程实例
const loadInstances = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    };
    
    // 添加筛选条件
    if (searchForm.status) {
      params.status = searchForm.status;
    }
    if (searchForm.startTime) {
      params.startTime = searchForm.startTime;
    }
    if (searchForm.endTime) {
      params.endTime = searchForm.endTime;
    }
    if (searchForm.businessKey) {
      params.businessKey = searchForm.businessKey;
    }
    
    const res = await axios.get('/api/workflow/instance/page', { params });
    if (res.data.code === 200 && res.data.data) {
      instances.value = res.data.data.records || [];
      total.value = res.data.data.total || 0;
      loadStatistics();
    }
  } catch (error) {
    ElMessage.error('加载流程实例失败：' + error.message);
  } finally {
    loading.value = false;
  }
};

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await axios.get('/api/workflow/instance/statistics');
    if (res.data.code === 200 && res.data.data) {
      Object.assign(statistics, res.data.data);
    }
  } catch (error) {
    console.error('加载统计数据失败:', error);
  }
};

// 查看详情
const viewDetail = async (instance) => {
  currentInstance.value = instance;
  detailVisible.value = true;
  
  // 加载任务列表
  try {
    const res = await axios.get(`/api/workflow/task/list/${instance.id}`);
    if (res.data.code === 200 && res.data.data) {
      taskList.value = res.data.data;
    } else {
      taskList.value = [];
    }
  } catch (error) {
    console.error('加载任务列表失败:', error);
    taskList.value = [];
  }
};

// 终止流程
const terminateProcess = async (instance) => {
  try {
    await ElMessageBox.confirm(
      `确定要终止流程实例 ${instance.id} 吗？此操作不可恢复。`,
      '终止流程确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    const res = await axios.put('/api/workflow/instance/terminate', null, {
      params: {
        instanceId: instance.id
      }
    });
    
    if (res.data.code === 200) {
      ElMessage.success('流程已终止');
      loadInstances();
    } else {
      ElMessage.error(res.data.message || '终止流程失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('终止流程失败：' + error.message);
    }
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadInstances();
};

// 重置
const handleReset = () => {
  searchForm.processKey = '';
  searchForm.status = '';
  searchForm.businessKey = '';
  searchForm.dateRange = [];
  searchForm.startTime = '';
  searchForm.endTime = '';
  currentPage.value = 1;
  loadInstances();
};

// 任务状态类型映射
const getTaskStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'IN_PROGRESS': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'info'
  };
  return types[status] || 'info';
};

// 任务状态文本映射
const getTaskStatusText = (status) => {
  const texts = {
    'PENDING': '待处理',
    'IN_PROGRESS': '处理中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return texts[status] || status;
};

// 操作文本映射
const getActionText = (action) => {
  const texts = {
    'APPROVE': '通过',
    'REJECT': '拒绝',
    'CONFIRM': '确认',
    'HANDLE': '处理',
    'TRANSFER': '转交',
    'TERMINATE': '终止'
  };
  return texts[action] || action;
};

// 时间格式化函数
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  
  // 如果是ISO格式字符串，转换为友好格式
  if (typeof dateTime === 'string') {
    // 替换 T 为空格
    return dateTime.replace('T', ' ').substring(0, 19);
  }
  
  return dateTime;
};

// 根据流程实例获取正确的流程名称
const getProcessName = (instance) => {
  // 优先根据 processKey 或 businessType 来判断
  const processKey = instance.processKey || '';
  const businessType = instance.businessType || '';
  
  // 如果有 processKey，直接映射
  if (processKey) {
    return getProcessKeyText(processKey);
  }
  
  // 否则根据 businessType 推断
  if (businessType === 'PURCHASE' || businessType === 'PURCHASE_PLAN') {
    return '采购审批流程';
  }
  if (businessType === 'BUDGET' || businessType === 'BUDGET_ADJUST') {
    return '预算调整流程';
  }
  if (businessType === 'ALERT' || businessType === 'ALERT_WORKORDER') {
    return '预警工单流程';
  }
  if (businessType === 'SALES' || businessType === 'SALES_ORDER') {
    return '销售订单流程';
  }
  
  // 最后使用数据库中的 processName
  return instance.processName || '未知流程';
};

// 流程标识文本映射
const getProcessKeyText = (key) => {
  const texts = {
    'PURCHASE_PLAN': '采购审批流程',
    'BUDGET_ADJUST': '预算调整流程',
    'ALERT_WORKORDER': '预警工单流程',
    'SALES_ORDER': '销售订单流程'
  };
  return texts[key] || key;
};

// 业务类型文本映射
const getBusinessTypeText = (type) => {
  const texts = {
    'PURCHASE': '采购计划',
    'PURCHASE_PLAN': '采购计划',
    'BUDGET': '预算调整',
    'BUDGET_ADJUST': '预算调整',
    'ALERT': '预警工单',
    'ALERT_WORKORDER': '预警工单',
    'SALES': '销售订单',
    'SALES_ORDER': '销售订单'
  };
  return texts[type] || type;
};

// 状态类型映射
const getStatusType = (status) => {
  const types = {
    'RUNNING': 'primary',
    'COMPLETED': 'success',
    'TERMINATED': 'danger',
    'PENDING': 'warning',
    'REJECTED': 'danger',
    '进行中': 'primary',
    '已完成': 'success',
    '已终止': 'danger',
    '待审批': 'warning',
    '已拒绝': 'danger'
  };
  return types[status] || 'info';
};

// 状态文本映射
const getStatusText = (status) => {
  const texts = {
    'RUNNING': '进行中',
    'COMPLETED': '已完成',
    'TERMINATED': '已终止',
    'PENDING': '待审批',
    'REJECTED': '已拒绝',
    '进行中': '进行中',
    '已完成': '已完成',
    '已终止': '已终止',
    '待审批': '待审批',
    '已拒绝': '已拒绝'
  };
  return texts[status] || status;
};

onMounted(() => {
  loadInstances();
});
</script>

<style scoped>
.workflow-instance {
  padding: 20px;
}

.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.running {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.filter-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.detail-container {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 25px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}
</style>
