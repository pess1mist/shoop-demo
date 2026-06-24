-- 创建内控预警日志表
USE test;

CREATE TABLE IF NOT EXISTS internal_control_log (
    log_id VARCHAR(64) PRIMARY KEY COMMENT '日志 ID',
    alert_type VARCHAR(100) NOT NULL COMMENT '预警类型（超预算/价格异常/成本异常等）',
    related_doc_no VARCHAR(100) COMMENT '相关单据号',
    alert_time DATETIME NOT NULL COMMENT '预警时间',
    alert_content TEXT COMMENT '预警内容',
    handle_status VARCHAR(20) DEFAULT '未处理' COMMENT '处理状态（已处理/处理中/未处理）',
    handler VARCHAR(100) COMMENT '处理人',
    INDEX idx_alert_time (alert_time),
    INDEX idx_handle_status (handle_status),
    INDEX idx_alert_type (alert_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内控预警日志表';

-- 插入一些测试数据
INSERT INTO internal_control_log (log_id, alert_type, related_doc_no, alert_time, alert_content, handle_status, handler) VALUES
('LOG001', '超预算预警', 'CG20250101001', '2025-01-15 10:30:00', '采购申请金额超出预算 15%', '已处理', '张三'),
('LOG002', '价格异常预警', 'XS20250115002', '2025-01-20 14:20:00', '销售价格低于成本价 5%', '处理中', '李四'),
('LOG003', '成本异常预警', 'SC20250201003', '2025-02-01 09:15:00', '生产成本超出标准成本 20%', '未处理', NULL),
('LOG004', '超预算预警', 'CG20250210004', '2025-02-10 16:45:00', '部门费用超出月度预算 10%', '已处理', '王五'),
('LOG005', '库存预警', 'CK20250215005', '2025-02-15 11:00:00', '原材料库存低于安全库存', '未处理', NULL);

-- 验证表结构
DESCRIBE internal_control_log;

-- 查看插入的数据
SELECT * FROM internal_control_log;
