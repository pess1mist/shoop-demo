package com.example.finance.common;

import com.example.finance.dto.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 通用Controller基础类
 * 提供统一的异常处理和结果封装
 */
@Slf4j
public abstract class BaseController {

    /**
     * 执行查询操作，统一异常处理
     */
    protected <T> Result<T> executeQuery(Supplier<T> querySupplier, String errorMsg) {
        try {
            T result = querySupplier.get();
            return Result.success(result);
        } catch (Exception e) {
            log.error(errorMsg, e);
            return Result.error(errorMsg + ": " + e.getMessage());
        }
    }

    /**
     * 执行操作，统一异常处理
     */
    protected Result<Boolean> executeOperation(Supplier<Boolean> operationSupplier, String successMsg, String errorMsg) {
        try {
            Boolean result = operationSupplier.get();
            return result ? Result.success(successMsg, true) : Result.error(errorMsg);
        } catch (Exception e) {
            log.error(errorMsg, e);
            return Result.error(errorMsg + ": " + e.getMessage());
        }
    }

    /**
     * 执行删除操作，统一异常处理
     */
    protected Result<Boolean> executeDelete(Supplier<Boolean> deleteSupplier, String errorMsg) {
        return executeOperation(deleteSupplier, "删除成功", errorMsg);
    }

    /**
     * 执行保存操作，统一异常处理
     */
    protected Result<Boolean> executeSave(Supplier<Boolean> saveSupplier, String errorMsg) {
        return executeOperation(saveSupplier, "保存成功", errorMsg);
    }

    /**
     * 执行更新操作，统一异常处理
     */
    protected Result<Boolean> executeUpdate(Supplier<Boolean> updateSupplier, String errorMsg) {
        return executeOperation(updateSupplier, "更新成功", errorMsg);
    }
}
