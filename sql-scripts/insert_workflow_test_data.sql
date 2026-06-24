-- ============================================
-- 工作流测试数据补充脚本
-- 创建时间: 2026-04-20
-- 说明: 补充工作流实例、任务、预警工单等测试数据
-- ============================================

USE shuju;

-- ============================================
-- 1. 插入工作流实例数据（10条）
-- ============================================

INSERT INTO workflow_instance (process_key, process_name, business_type, business_key, status, current_node_id, initiator_id, initiator_name, start_time, end_time, create_time) VALUES
('purchase_approval', '采购审批流程', 'PURCHASE', 'PO20260420001', 'RUNNING', 2, 1, '张三', NOW(), NULL, NOW()),
('purchase_approval', '采购审批流程', 'PURCHASE', 'PO20260420002', 'COMPLETED', 3, 2, '李四', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('purchase_approval', '采购审批流程', 'PURCHASE', 'PO20260420003', 'RUNNING', 1, 1, '张三', NOW(), NULL, NOW()),
('budget_adjust', '预算调整流程', 'BUDGET', 'BA20260420001', 'RUNNING', 1, 1, '张三', NOW(), NULL, NOW()),
('budget_adjust', '预算调整流程', 'BUDGET', 'BA20260420002', 'COMPLETED', 3, 2, '李四', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('alert_handling', '预警处理流程', 'ALERT', 'AH20260420001', 'PENDING', 1, 3, '王五', NOW(), NULL, NOW()),
('alert_handling', '预警处理流程', 'ALERT', 'AH20260420002', 'COMPLETED', 2, 2, '李经理', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('purchase_approval', '采购审批流程', 'PURCHASE', 'PO20260419001', 'REJECTED', 2, 3, '王五', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
('budget_adjust', '预算调整流程', 'BUDGET', 'BA20260418001', 'COMPLETED', 3, 1, '张三', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
('alert_handling', '预警处理流程', 'ALERT', 'AH20260417001', 'COMPLETED', 2, 2, '李经理', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY));

-- ============================================
-- 2. 插入工作流任务数据（15条）
-- ============================================

INSERT INTO workflow_task (instance_id, node_id, node_name, assignee_id, assignee_name, task_type, status, create_time, complete_time, comment) VALUES
(1, 2, '部门经理审批', 2, '李经理', 'APPROVAL', 'PENDING', NOW(), NULL, NULL),
(2, 2, '部门经理审批', 2, '李经理', 'APPROVAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), '同意采购'),
(2, 3, '财务审批', 3, '王财务', 'APPROVAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), '已审核，符合预算'),
(3, 1, '申请人提交', 1, '张三', 'SUBMIT', 'COMPLETED', NOW(), NOW(), '申请采购原材料'),
(4, 1, '申请人提交', 1, '张三', 'SUBMIT', 'COMPLETED', NOW(), NOW(), '申请调整材料费预算'),
(5, 2, '部门经理审批', 2, '李经理', 'APPROVAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), '同意调整'),
(5, 3, '财务审批', 3, '王财务', 'APPROVAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), '预算合理，批准'),
(6, 1, '预警确认', 3, '王五', 'CONFIRM', 'PENDING', NOW(), NULL, NULL),
(7, 1, '预警确认', 2, '李经理', 'CONFIRM', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), '已确认异常'),
(7, 2, '处理预警', 2, '李经理', 'HANDLE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), '已采取措施'),
(8, 2, '部门经理审批', 2, '李经理', 'APPROVAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), '价格偏高，驳回重议'),
(9, 2, '部门经理审批', 2, '李经理', 'APPROVAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), '同意'),
(9, 3, '财务审批', 3, '王财务', 'APPROVAL', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), '审核通过'),
(10, 1, '预警确认', 2, '李经理', 'CONFIRM', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), '成本异常已确认'),
(10, 2, '处理预警', 2, '李经理', 'HANDLE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), '已优化生产流程');

-- ============================================
-- 3. 插入采购审批记录（5条，与工作流关联）
-- ============================================
-- 注意：purchase_approval表结构已变更，使用新字段
INSERT INTO purchase_approval (approval_no, purchase_plan_id, approver_id, approver_name, approval_status, approval_time, approval_comment, created_time) VALUES
('PA20260420001', 1, NULL, NULL, 'PENDING', NULL, NULL, NOW()),
('PA20260420002', 2, 2, '李经理', 'APPROVED', DATE_SUB(NOW(), INTERVAL 1 DAY), '同意采购', DATE_SUB(NOW(), INTERVAL 2 DAY)),
('PA20260420003', 3, NULL, NULL, 'PENDING', NULL, NULL, NOW()),
('PA20260419001', 4, 2, '李经理', 'REJECTED', DATE_SUB(NOW(), INTERVAL 6 DAY), '价格偏高，请重新询价', DATE_SUB(NOW(), INTERVAL 7 DAY)),
('PA20260418001', 5, 3, '王财务', 'APPROVED', DATE_SUB(NOW(), INTERVAL 8 DAY), '审核通过', DATE_SUB(NOW(), INTERVAL 10 DAY));

-- ============================================
-- 4. 插入预警工单数据（8条）
-- ============================================

INSERT INTO alert_work_order (alert_log_id, work_order_no, alert_type, alert_level, description, handler_id, handler_name, status, create_time, handle_time, handle_result, remark) VALUES
(1, 'WO20260420001', '预算超支', 'HIGH', '膨化线材料费超支15%，本月累计超支达8万元', 2, '李经理', 'PROCESSING', NOW(), NULL, NULL, '需紧急处理'),
(2, 'WO20260420002', '成本异常', 'MEDIUM', '乳化线单位成本上升8%，超出正常波动范围', 3, '王主管', 'PENDING', NOW(), NULL, NULL, '待分析原因'),
(3, 'WO20260420003', '价格波动', 'LOW', '小麦粉价格上涨5%，建议关注市场动态', 1, '张三', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), '已确认市场价格上涨趋势，建议提前采购', '市场因素导致'),
(4, 'WO20260419001', '预算超支', 'HIGH', '水胶线人工费超支20%', 2, '李经理', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), '已调整人员配置，控制加班', '人员优化后已改善'),
(5, 'WO20260418001', '成本异常', 'MEDIUM', '膨化线制造费用异常增长12%', 3, '王主管', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), '设备维护费用增加，已安排预防性维护', '设备老化导致'),
(6, 'WO20260417001', '价格波动', 'MEDIUM', '植物油价格上涨8%，影响成本', 1, '张三', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), '已联系多家供应商比价', '已锁定优惠价格'),
(7, 'WO20260416001', '预算超支', 'LOW', '乳化线制造费用轻微超支5%', 2, '李经理', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), '正常波动，持续监控', '无需特别处理'),
(8, 'WO20260415001', '成本异常', 'HIGH', '水胶线材料成本异常增长18%', 3, '王主管', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), '发现原材料质量问题，已更换供应商', '质量问题已解决');

-- ============================================
-- 验证数据插入结果
-- ============================================

SELECT '工作流实例' as 数据类型, COUNT(*) as 记录数 FROM workflow_instance
UNION ALL
SELECT '工作流任务', COUNT(*) FROM workflow_task
UNION ALL
SELECT '采购审批', COUNT(*) FROM purchase_approval
UNION ALL
SELECT '预警工单', COUNT(*) FROM alert_work_order;

-- ============================================
-- 脚本执行完成
-- ============================================
