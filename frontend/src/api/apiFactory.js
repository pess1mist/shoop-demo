import request from '@/utils/request'

/**
 * API工厂函数
 * 简化API调用的创建过程
 */

// 创建基础API方法
const createApiMethod = (method) => (url) => (params) => {
  return request({
    url,
    method,
    ...(method === 'get' || method === 'delete' ? { params } : { data: params })
  })
}

// 创建不同HTTP方法的API方法
export const get = createApiMethod('get')
export const post = createApiMethod('post')
export const put = createApiMethod('put')
export const del = createApiMethod('delete')

/**
 * 创建RESTful API对象
 * 自动生成增删改查方法
 */
export const createRestApi = (baseUrl) => ({
  list: get(`${baseUrl}/list`),
  page: (pageNum = 1, pageSize = 10) => get(`${baseUrl}/page`)({ pageNum, pageSize }),
  getById: (id) => get(`${baseUrl}/${id}`)(),
  save: post(baseUrl),
  update: put(baseUrl),
  delete: (id) => del(`${baseUrl}/${id}`)()
})

/**
 * 创建带路径参数的API方法
 */
export const createPathApi = (baseUrl) => ({
  get: (path) => get(`${baseUrl}${path}`)(),
  post: (path, data) => post(`${baseUrl}${path}`)(data),
  put: (path, data) => put(`${baseUrl}${path}`)(data),
  delete: (path) => del(`${baseUrl}${path}`)()
})
