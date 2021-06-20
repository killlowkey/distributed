package com.distributed.registry;

import com.distributed.ServiceMatcher;
import com.distributed.annotation.ConditionalOnNotRegistry;
import com.distributed.entity.ServiceData;
import com.distributed.exception.DistributedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务发现：轮训匹配
 *
 * @author Ray
 * @date created in 2021/6/20 9:32
 */
@ConditionalOnNotRegistry
@Component
@ConditionalOnProperty(value = "service.discovery.strategy", havingValue = "ROTATION")
public class RotationServiceMather implements ServiceMatcher {

    private final AtomicInteger counter = new AtomicInteger();
    private Map<String, List<ServiceData>> services = null;

    @Override
    public ServiceData match(String serviceName) {

        if (services == null) {
            throw new DistributedException("services is null, please add services");
        }

        List<ServiceData> serviceDataList = services.get(serviceName);
        if (serviceDataList == null || serviceDataList.isEmpty()) {
            throw new DistributedException(String.format("not found %s service", serviceName));
        }


        return serviceDataList.get(counter.getAndIncrement() % serviceDataList.size());
    }

    @Override
    public void setServices(Map<String, List<ServiceData>> services) {
        this.services = services;
    }

}
