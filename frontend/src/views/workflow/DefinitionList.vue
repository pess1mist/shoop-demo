<template>
  <div class="workflow-definition">
    <h2>流程定义管理</h2>
    
    <!-- 流程定义列表 -->
    <el-table :data="definitions" style="width: 100%" border>
      <el-table-column prop="processKey" label="流程标识" width="200">
        <template #default="scope">
          {{ getProcessKeyText(scope.row.processKey) }}
        </template>
      </el-table-column>
      <el-table-column prop="processName" label="流程名称" width="200" />
      <el-table-column prop="version" label="版本号" width="100" />
      <el-table-column label="审批节点" width="250">
        <template #default="scope">
          {{ getNodesText(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'" effect="light">
            {{ scope.row.status === 'ACTIVE' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip>
        <template #default="scope">
          {{ getDescription(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="scope">
          {{ scope.row.createdTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small"
            @click="viewDetail(scope.row)">
            查看
          </el-button>
          <el-button 
            type="success" 
            size="small"
            @click="startProcess(scope.row)">
            发起流程
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
      @size-change="loadDefinitions"
      @current-change="loadDefinitions"
      style="margin-top: 20px; justify-content: flex-end;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

const definitions = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 加载流程定义
const loadDefinitions = async () => {
  try {
    const res = await axios.get(`/api/workflow/definition/page?pageNum=${currentPage.value}&pageSize=${pageSize.value}`);
    definitions.value = res.data.data.records || [];
    total.value = res.data.data.total || 0;
  } catch (error) {
    ElMessage.error('加载流程定义失败：' + error.message);
  }
};

// 节点信息文本映射
const getNodesText = (definition) => {
  try {
    if (!definition.nodes) return '-';
    const nodes = typeof definition.nodes === 'string' 
      ? JSON.parse(definition.nodes) 
      : definition.nodes;
    
    if (Array.isArray(nodes)) {
      const approvalNodes = nodes.filter(n => n.nodeType === 'APPROVAL');
      return approvalNodes.map(n => n.nodeName || n.nodeKey).join(' → ');
    }
  } catch (e) {
    console.error('解析节点信息失败:', e);
  }
  return '-';
};

// 流程标识文本映射
const getProcessKeyText = (key) => {
  const texts = {
    'PURCHASE_PLAN': '采购计划',
    'BUDGET_ADJUST': '预算调整',
    'SALES_ORDER': '销售订单',
    'ALERT_WORKORDER': '预警工单'
  };
  return texts[key] || key;
};

// 流程描述映射
const getDescription = (definition) => {
  const descriptions = {
    'PURCHASE_PLAN': '采购计划申请审批流程',
    'BUDGET_ADJUST': '预算调整申请审批流程',
    'SALES_ORDER': '销售订单确认审批流程',
    'ALERT_WORKORDER': '预警工单处理流程'
  };
  return descriptions[definition.processKey] || definition.description || '-';
};

// 分类文本映射
const getCategoryText = (category) => {
  const texts = {
    'PURCHASE': '采购管理',
    'BUDGET': '预算管理',
    'ALERT': '预警管理'
  };
  return texts[category] || category;
};

// 查看详情
const viewDetail = (definition) => {
  ElMessage.info('查看流程定义：' + definition.processName);
  console.log('流程定义详情:', definition);
};

// 发起流程
const startProcess = (definition) => {
  ElMessage.success('发起流程：' + definition.processName);
  // TODO: 跳转到发起流程页面
};

onMounted(() => {
  loadDefinitions();
});
</script>

<style scoped>
.workflow-definition {
  padding: 20px;
}
</style>
