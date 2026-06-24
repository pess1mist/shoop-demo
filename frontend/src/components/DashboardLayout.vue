<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo-wrapper">
        <el-icon class="logo-icon"><DataLine /></el-icon>
        <span class="logo-text">智能制造内控系统</span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        class="sidebar-menu"
      >
        <el-sub-menu index="data-analysis">
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>数据分析</span>
          </template>
          <el-menu-item index="/data-viz">数据可视化分析</el-menu-item>
          <el-menu-item index="/cost-analysis">成本分析</el-menu-item>
          <el-menu-item index="/budget-execution">预算执行</el-menu-item>
          <el-menu-item index="/production-monitor">生产监控</el-menu-item>
          <el-menu-item index="/internal-control">内控预警</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="workflow">
          <template #title>
            <el-icon><List /></el-icon>
            <span>审批流程</span>
          </template>
          <el-menu-item index="/workflow/my-applications">我的申请</el-menu-item>
          <el-menu-item index="/workflow/tasks">我的待办</el-menu-item>
          <el-menu-item index="/workflow/definitions">流程定义</el-menu-item>
          <el-menu-item index="/workflow/instances">流程历史</el-menu-item>
          <el-menu-item index="/workflow/approval-rules">审批规则管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2 class="page-title">{{ $route.meta.title || '首页' }}</h2>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { DataLine, DataBoard, UserFilled, TrendCharts, List } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 处理下拉菜单命令
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch {
      // 用户取消
    }
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  background-color: #2b3a4b;
  gap: 10px;
}

.logo-icon {
  font-size: 28px;
  color: #409EFF;
}

.logo-text {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.sidebar-menu {
  border-right: none;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.username {
  font-size: 14px;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
