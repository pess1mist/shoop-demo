package com.example.finance.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;

/**
 * 查询构建工具类
 * 简化MyBatis Plus查询条件的构建
 */
public class QueryHelper {

    /**
     * 构建查询条件
     */
    public static <T> LambdaQueryWrapper<T> buildWrapper(Consumer<LambdaQueryWrapper<T>> builder) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        builder.accept(wrapper);
        return wrapper;
    }

    /**
     * 添加模糊查询条件（如果值不为空）
     */
    public static <T> void likeIfPresent(LambdaQueryWrapper<T> wrapper, 
                                         com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> column, 
                                         String value) {
        if (StringUtils.hasText(value)) {
            wrapper.like(column, value);
        }
    }

    /**
     * 添加等值查询条件（如果值不为空）
     */
    public static <T> void eqIfPresent(LambdaQueryWrapper<T> wrapper, 
                                       com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> column, 
                                       Object value) {
        if (value != null) {
            if (value instanceof String && !StringUtils.hasText((String) value)) {
                return;
            }
            wrapper.eq(column, value);
        }
    }

    /**
     * 添加范围查询条件（如果值不为空）
     */
    public static <T> void betweenIfPresent(LambdaQueryWrapper<T> wrapper, 
                                            com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> column, 
                                            Object start, Object end) {
        if (start != null && end != null) {
            wrapper.between(column, start, end);
        }
    }
}
