package com.distributed.service;

/**
 * @author Ray
 */
public interface HealthService {
    /**
     * 添加心跳检查
     *
     * @param url 服务url
     */
    void addHealthCheck(String url);

    /**
     * 移除服务心跳
     *
     * @param url 服务url
     */
    void removeHealthCheck(String url);
}
