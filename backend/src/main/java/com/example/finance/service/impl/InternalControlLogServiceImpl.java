package com.example.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.finance.entity.InternalControlLog;
import com.example.finance.mapper.InternalControlLogMapper;
import com.example.finance.service.InternalControlLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内控预警日志服务实现类
 */
@Service
public class InternalControlLogServiceImpl extends ServiceImpl<InternalControlLogMapper, InternalControlLog> implements InternalControlLogService {

    @Override
    public List<InternalControlLog> listAll() {
        return this.list();
    }

    @Override
    public List<InternalControlLog> listByAlertType(String alertType) {
        LambdaQueryWrapper<InternalControlLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InternalControlLog::getAlertType, alertType);
        return this.list(wrapper);
    }

    @Override
    public List<InternalControlLog> listByHandleStatus(String handleStatus) {
        LambdaQueryWrapper<InternalControlLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InternalControlLog::getHandleStatus, handleStatus);
        return this.list(wrapper);
    }

    @Override
    public boolean updateHandleStatus(String logId, String handleStatus, String handler) {
        InternalControlLog log = this.getById(logId);
        if (log == null) {
            return false;
        }
        
        log.setHandleStatus(handleStatus);
        log.setHandler(handler);
        return this.updateById(log);
    }
}
