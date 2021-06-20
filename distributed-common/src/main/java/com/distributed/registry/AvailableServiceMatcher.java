package com.distributed.registry;

import com.distributed.ServiceMatcher;
import com.distributed.annotation.ConditionalOnNotRegistry;
import com.distributed.entity.ServiceData;
import com.distributed.exception.DistributedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 服务发现：根据可用性匹配器
 *
 * @author Ray
 * @date created in 2021/6/20 9:42
 */
@ConditionalOnNotRegistry
@Component
@ConditionalOnProperty(value = "service.discovery.strategy", havingValue = "AVAILABLE")
public class AvailableServiceMatcher implements ServiceMatcher {

    private Map<String, List<ServiceData>> services = null;

    @Override
    public ServiceData match(String serviceName) {

        if (this.services == null) {
            throw new DistributedException("services is null, please add services");
        }

        List<ServiceData> serviceDataList = services.get(serviceName);
        if (serviceDataList == null || serviceDataList.isEmpty()) {
            throw new DistributedException(String.format("not found %s service", serviceName));
        }

        ServiceData result = null;
        for (ServiceData t1 : serviceDataList) {
            if (result == null) {
                result = t1;
            } else {
                double memoryRatio = result.getMachineInfo().getMemoryRatio();
                if (t1.getMachineInfo().getMemoryRatio() > memoryRatio) {
                    result = t1;
                }
            }
        }

        return result;
    }

    @Override
    public void setServices(Map<String, List<ServiceData>> services) {
        this.services = services;
    }

}
