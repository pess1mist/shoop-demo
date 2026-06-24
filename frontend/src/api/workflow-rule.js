import request from '@/utils/request'

/**
 * 查询所有启用的审批规则
 */
export function getActiveRules() {
  return request({
    url: '/workflow/rule/list',
    method: 'get'
  })
}

/**
 * 根据流程标识查询规则
 */
export function getRulesByProcessKey(processKey) {
  return request({
    url: `/workflow/rule/list/${processKey}`,
    method: 'get'
  })
}

/**
 * 根据流程标识和规则类型查询
 */
export function getRulesByType(processKey, ruleType) {
  return request({
    url: `/workflow/rule/list/${processKey}/${ruleType}`,
    method: 'get'
  })
}

/**
 * 匹配审批规则
 */
export function matchRule(processKey, businessData) {
  return request({
    url: '/workflow/rule/match',
    method: 'post',
    params: { processKey, businessData }
  })
}

/**
 * 测试规则是否匹配
 */
export function testRule(ruleId, businessData) {
  return request({
    url: '/workflow/rule/test',
    method: 'post',
    params: { ruleId, businessData }
  })
}

/**
 * 创建规则
 */
export function createRule(data) {
  return request({
    url: '/workflow/rule/create',
    method: 'post',
    data
  })
}

/**
 * 更新规则
 */
export function updateRule(ruleId, data) {
  return request({
    url: `/workflow/rule/update/${ruleId}`,
    method: 'put',
    data
  })
}

/**
 * 启用规则
 */
export function enableRule(ruleId) {
  return request({
    url: `/workflow/rule/enable/${ruleId}`,
    method: 'put'
  })
}

/**
 * 停用规则
 */
export function disableRule(ruleId) {
  return request({
    url: `/workflow/rule/disable/${ruleId}`,
    method: 'put'
  })
}

/**
 * 删除规则
 */
export function deleteRule(ruleId) {
  return request({
    url: `/workflow/rule/delete/${ruleId}`,
    method: 'delete'
  })
}
