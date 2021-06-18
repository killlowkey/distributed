package com.distributed.registry;

import com.distributed.annotation.ConditionalOnNotRegistry;
import com.distributed.entity.ServerResponse;
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

    private final Map<String, List<String>> services = new HashMap<>();
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

        ServerResponse<List<LinkedHashMap<String, String>>> serverResponse =
                restTemplate.getForObject(registryAddress + "/services", ServerResponse.class);
        List<LinkedHashMap<String, String>> registrations = Objects.requireNonNull(serverResponse).getData();
        if (registrations != null && registrations.size() != 0) {
            services.clear();
            registrations.forEach(map -> {
                String serviceName = map.get("serviceName");
                String serviceUrl = map.get("serviceUrl");
                List<String> urlList = services.get(serviceName);
                if (urlList == null) {
                    services.put(serviceName, new ArrayList<>(List.of(serviceUrl)));
                } else {
                    urlList.add(serviceUrl);
                }
            });
            log.trace("update service success");
        }
    }

    public String getService(String serviceName) {
        List<String> urls = services.get(serviceName);
        if (urls == null || urls.size() == 0) {
            throw new DistributedException(String.format("not found %s service", serviceName));
        }
        return urls.get(random.nextInt(urls.size()));
    }
}
