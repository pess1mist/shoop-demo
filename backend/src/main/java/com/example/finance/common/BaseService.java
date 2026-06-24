package com.example.finance.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 通用Service接口
 * 提供常用的查询方法
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 根据字段查询列表
     */
    default List<T> listByField(com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> field, Object value) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(field, value);
        return this.list(wrapper);
    }

    /**
     * 根据字段查询单个对象
     */
    default T getByField(com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> field, Object value) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(field, value);
        return this.getOne(wrapper);
    }

    /**
     * 根据字段模糊查询
     */
    default List<T> listLikeField(com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> field, String value) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(field, value);
        return this.list(wrapper);
    }
}
