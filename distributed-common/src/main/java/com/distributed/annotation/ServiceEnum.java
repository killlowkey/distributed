package com.distributed.annotation;

/**
 * @author Ray
 * @date created in 2021/6/18 11:51
 */
public enum ServiceEnum {

    REGISTRY("Registry-Service"),
    AUTH("Auth-Service"),
    GATEWAY("Gateway-Service"),
    ORDER("Order-Service"),
    LOG("Log-Service");

    private final String serviceName;

    ServiceEnum(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }
}
