package com.example.demo.service;

import com.example.demo.common.BaseService;
import com.example.demo.entity.AuthDemo;

/**
 * @author elliott
 */
public interface ITriggerService extends BaseService<AuthDemo> {

    /**
     * 数据插入
     */
    void insert() throws InterruptedException;
}
