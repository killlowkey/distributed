package com.distributed.service;

import com.distributed.entity.Registration;

import java.util.List;

/**
 * @author Ray
 */
public interface RegistryService {
    /**
     * 注册服务
     *
     * @param reg 服务实体
     */
    void register(Registration reg);

    /**
     * 取消服务
     *
     * @param url 服务url
     */
    void unregister(String url);

    /**
     * 查询所有服务
     *
     * @return 注册服务
     */
    List<Registration> findAllServices();
}
