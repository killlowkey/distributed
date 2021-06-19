package com.distributed.registry;

import com.distributed.annotation.ConditionalOnNotRegistry;
import com.distributed.entity.MachineInfo;
import com.distributed.entity.ServerResponse;
import com.distributed.entity.Service;
import com.distributed.exception.DistributedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author Ray
 */
@ConditionalOnNotRegistry
@EnableScheduling
@Component
@Slf4j
public class ServiceDiscovery {

    private final Map<String, List<Service.ServiceData>> services = new HashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();
    private final static String IGNORE_SERVICE_NAME = "Registry-Service";

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${registry.address}")
    private String registryAddress;

    // 每隔5s从注册中心拉取服务
    @Scheduled(initialDelay = 0L, fixedDelay = 5L * 1000)
    public void updateService() {
        if (IGNORE_SERVICE_NAME.equals(applicationName)) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("pull services from RegistryCenter");
        }

        ServerResponse<List<LinkedHashMap<String, Object>>> serverResponse =
                restTemplate.getForObject(registryAddress + "/services", ServerResponse.class);
        List<LinkedHashMap<String, Object>> registrations = Objects.requireNonNull(serverResponse).getData();
        if (registrations != null && registrations.size() != 0) {
            services.clear();
            registrations.forEach(map -> {
                String serviceName = (String) map.get("serviceName");
                List<Service.ServiceData> serviceData = services.get(serviceName);
                Service.ServiceData data = getServiceData((List<Map<String, Object>>) map.get("data"));
                if (serviceData == null) {
                    services.put(serviceName, new ArrayList<>(List.of(data)));
                } else {
                    serviceData.add(data);
                }
            });
            log.trace("update service success");
        }
    }

    private Service.ServiceData getServiceData(List<Map<String, Object>> data) {
        Map<String, Object> serviceInfo = data.get(0);
        String url = (String) serviceInfo.get("url");
        Map<String, Object> machineInfo = (Map<String, Object>) serviceInfo.get("machineInfo");
        return new Service.ServiceData(url, new MachineInfo(machineInfo));
    }

    /**
     * 根据服务名返回服务的 url
     *
     * @param serviceName 服务url
     * @return 返回服务集群中可用内存最高的 url
     */
    public String getService(String serviceName) {

        List<Service.ServiceData> serviceDataList = services.get(serviceName);
        if (serviceDataList == null || serviceDataList.size() == 0) {
            throw new DistributedException(String.format("not found %s service", serviceName));
        }

        Service.ServiceData temp = null;
        for (Service.ServiceData t1 : serviceDataList) {
            if (temp == null) {
                temp = t1;
            } else {
                double memoryRatio = temp.getMachineInfo().getMemoryRatio();
                if (t1.getMachineInfo().getMemoryRatio() > memoryRatio) {
                    temp = t1;
                }
            }
        }

        // return serviceDataList.get(random.nextInt(serviceDataList.size()));
        return temp.getUrl();
    }
}
