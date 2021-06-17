package com.distributed.service.impl;

import com.distributed.entity.Registration;
import com.distributed.exception.DistributedException;
import com.distributed.service.HeathService;
import com.distributed.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ray
 */
@Service
public class RegistryServiceImpl implements RegistryService {

    private final ReentrantLock serviceLock = new ReentrantLock();
    private final List<Registration> services = new ArrayList<>();
    private HeathService heathService;
    private final ApplicationContext context;

    public RegistryServiceImpl(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        this.heathService = context.getBean(HeathService.class);
    }

    @Override
    public void register(Registration reg) {
        serviceLock.lock();
        services.add(reg);
        // 添加服务心跳
        heathService.addHeathCheck(reg.getServiceUrl());
        serviceLock.unlock();
    }

    @Override
    public void unregister(String url) {
        for (int i = 0; i < services.size(); i++) {
            Registration service = services.get(i);
            if (service.getServiceUrl().equals(url)) {
                removeService(i);
                // 移除服务心跳
                heathService.removeHeadCheck(url);
                return;
            }
        }

        throw new DistributedException("未找到该服务：" + url);
    }

    @Override
    public List<Registration> findAllServices() {
        return List.copyOf(services);
    }

    private void removeService(int index) {
        serviceLock.lock();
        services.remove(index);
        serviceLock.unlock();
    }

}

