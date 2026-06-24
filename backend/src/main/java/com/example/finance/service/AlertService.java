package com.example.finance.service;

import com.example.finance.entity.InternalControlLog;
import com.example.finance.mapper.InternalControlLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预警服务类（用于内控预警管理）
 */
@Service
public class AlertService {

    @Autowired
    private InternalControlLogMapper alertMapper;

    /**
     * 获取预警列表
     * @param status 处理状态（可选）
     * @return 预警列表
     */
    public List<InternalControlLog> getAlertList(String status) {
        if (status != null && !status.isEmpty()) {
            return alertMapper.selectByStatus(status);
        } else {
            // 查询所有预警，使用 MyBatis-Plus 的 selectList 方法
            return alertMapper.selectList(null);
        }
    }

    /**
     * 更新预警处理状态
     * @param logId 日志 ID
     * @param status 状态
     * @param handler 处理人
     * @return 是否成功
     */
    public boolean updateAlertStatus(String logId, String status, String handler) {
        InternalControlLog log = alertMapper.selectById(logId);
        if (log == null) {
            return false;
        }

        log.setHandleStatus(status);
        log.setHandler(handler);
        return alertMapper.updateById(log) > 0;
    }
}
