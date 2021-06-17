package com.distributed.service;

/**
 * @author Ray
 */
public interface HeathService {
    /**
     * 添加心跳检查
     *
     * @param url 服务url
     */
    void addHeathCheck(String url);

    /**
     * 移除服务心跳
     *
     * @param url 服务url
     */
    void removeHeadCheck(String url);
}
