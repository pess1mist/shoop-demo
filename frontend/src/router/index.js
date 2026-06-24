import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '用户登录' }
  },
  {
    path: '/',
    redirect: '/data-viz'
  },
  {
    path: '/dashboard',
    redirect: '/data-viz'
  },
  {
    path: '/',
    component: () => import('@/components/DashboardLayout.vue'),
    children: [
      {
        path: 'data-viz',
        name: 'DataVisualization',
        component: () => import('@/views/DataVisualization.vue'),
        meta: { title: '数据可视化分析', requiresAuth: true }
      },
      {
        path: 'cost-analysis',
        name: 'CostAnalysis',
        component: () => import('@/views/CostAnalysis.vue'),
        meta: { title: '成本分析', requiresAuth: true }
      },
      {
        path: 'budget-execution',
        name: 'BudgetExecution',
        component: () => import('@/views/BudgetExecution.vue'),
        meta: { title: '预算执行', requiresAuth: true }
      },
      {
        path: 'production-monitor',
        name: 'ProductionMonitor',
        component: () => import('@/views/ProductionMonitor.vue'),
        meta: { title: '生产监控', requiresAuth: true }
      },
      {
        path: 'internal-control',
        name: 'InternalControl',
        component: () => import('@/views/InternalControl.vue'),
        meta: { title: '内控预警', requiresAuth: true }
      },
      {
        path: 'workflow/tasks',
        name: 'TaskList',
        component: () => import('@/views/workflow/TaskList.vue'),
        meta: { title: '我的待办', requiresAuth: true }
      },
      {
        path: 'workflow/tasks/:taskId',
        name: 'TaskHandle',
        component: () => import('@/views/workflow/TaskHandle.vue'),
        meta: { title: '任务处理', requiresAuth: true }
      },
      {
        path: 'workflow/my-applications',
        name: 'MyApplications',
        component: () => import('@/views/workflow/MyApplications.vue'),
        meta: { title: '我的申请', requiresAuth: true }
      },
      {
        path: 'workflow/purchase-apply',
        name: 'PurchaseApply',
        component: () => import('@/views/workflow/PurchaseApply.vue'),
        meta: { title: '新建采购申请', requiresAuth: true }
      },
      {
        path: 'workflow/definitions',
        name: 'DefinitionList',
        component: () => import('@/views/workflow/DefinitionList.vue'),
        meta: { title: '流程定义', requiresAuth: true }
      },
      {
        path: 'workflow/instances',
        name: 'InstanceList',
        component: () => import('@/views/workflow/InstanceList.vue'),
        meta: { title: '流程历史', requiresAuth: true }
      },
      {
        path: 'workflow/approval-rules',
        name: 'ApprovalRule',
        component: () => import('@/views/workflow/ApprovalRule.vue'),
        meta: { title: '审批规则管理', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  
  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn()) {
      // 未登录，重定向到登录页，并保存原始访问路径
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else if (to.path === '/login') {
    // 如果已登录，访问登录页时重定向到首页
    if (userStore.isLoggedIn()) {
      next({ path: '/dashboard' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
