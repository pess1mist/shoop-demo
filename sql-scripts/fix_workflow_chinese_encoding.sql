-- ============================================
-- 修复工作流数据中文乱码脚本
-- 创建时间: 2026-04-21
-- ============================================

USE shuju;

-- 1. 检查数据库字符集设置
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';

-- 2. 检查表的字符集
SHOW CREATE TABLE workflow_task;
SHOW CREATE TABLE workflow_instance;

-- 3. 如果表不是utf8mb4，执行以下ALTER语句
ALTER TABLE workflow_task CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE workflow_instance CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. 清空现有的乱码数据
DELETE FROM workflow_task WHERE comment IS NOT NULL AND comment != '';
DELETE FROM workflow_task WHERE node_name LIKE '%?%' OR node_name REGEXP '[^a-zA-Z0-9\u4e00-\u9fa5]';

-- 5. 重新插入正确的中文数据
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

-- 6. 验证数据
SELECT id, node_name, comment FROM workflow_task WHERE id <= 15;
SELECT '修复完成！' as status;
