package com.distributed.registry;

import com.distributed.ServiceMatcher;
import com.distributed.annotation.ConditionalOnNotRegistry;
import com.distributed.entity.ServiceData;
import com.distributed.exception.DistributedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 服务发现：随机匹配
 *
 * @author Ray
 * @date created in 2021/6/20 9:46
 */
@ConditionalOnNotRegistry
@Component
@ConditionalOnProperty(value = "service.discovery.strategy", havingValue = "RANDOM")
public class RandomServiceMather implements ServiceMatcher {

    private Map<String, List<ServiceData>> services = null;
    private final Random random = new Random();

    @Override
    public ServiceData match(String serviceName) {
        if (services == null) {
            throw new DistributedException("services is null, please add services");
        }

        List<ServiceData> serviceDataList = services.get(serviceName);
        if (serviceDataList == null || serviceDataList.isEmpty()) {
            throw new DistributedException(String.format("not found %s service", serviceName));
        }

        int bound = random.nextInt(serviceDataList.size());
        return serviceDataList.get(bound);
    }

    @Override
    public void setServices(Map<String, List<ServiceData>> services) {
        this.services = services;
    }
}
