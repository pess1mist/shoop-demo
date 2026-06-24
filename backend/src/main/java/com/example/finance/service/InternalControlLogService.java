package com.example.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.finance.entity.InternalControlLog;

import java.util.List;

/**
 * 内控预警日志服务接口
 */
public interface InternalControlLogService extends IService<InternalControlLog> {

    /**
     * 查询所有预警日志
     * @return 预警日志列表
     */
    List<InternalControlLog> listAll();

    /**
     * 按预警类型查询
     * @param alertType 预警类型
     * @return 预警日志列表
     */
    List<InternalControlLog> listByAlertType(String alertType);

    /**
     * 按处理状态查询
     * @param handleStatus 处理状态
     * @return 预警日志列表
     */
    List<InternalControlLog> listByHandleStatus(String handleStatus);

    /**
     * 更新预警处理状态
     * @param logId 日志 ID
     * @param handleStatus 处理状态
     * @param handler 处理人
     * @return 是否更新成功
     */
    boolean updateHandleStatus(String logId, String handleStatus, String handler);
}
