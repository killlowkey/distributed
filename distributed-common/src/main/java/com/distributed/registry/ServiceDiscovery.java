package com.distributed.registry;

import com.distributed.ServiceMatcher;
import com.distributed.annotation.ConditionalOnNotRegistry;
import com.distributed.entity.MachineInfo;
import com.distributed.entity.ServerResponse;
import com.distributed.entity.ServiceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Ray
 */
@ConditionalOnNotRegistry
@RequiredArgsConstructor
@EnableScheduling
@Component
@Slf4j
public class ServiceDiscovery {

    private final Map<String, List<ServiceData>> services = new HashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final static String IGNORE_SERVICE_NAME = "Registry-Service";
    private final ServiceMatcher matcher;
    private String token;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${registry.address}")
    private String registryAddress;

    @Value("${server.port}")
    private int port;

    @PostConstruct
    public void init() {
        log.info("matcher name {}", matcher.getClass().getSimpleName());
        this.matcher.setServices(this.services);
    }

    // 每隔5s从注册中心拉取服务
    @Scheduled(initialDelay = 0L, fixedDelay = 5L * 1000)
    @SuppressWarnings("all")
    public void pullService() {
        if (IGNORE_SERVICE_NAME.equals(applicationName)) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("pull services from RegistryCenter");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("token", token);
        headers.set("url", "http://localhost:" + port);
        HttpEntity httpEntity = new HttpEntity(headers);
        ServerResponse<List<LinkedHashMap<String, Object>>> serverResponse =
                restTemplate.exchange(registryAddress + "/services", HttpMethod.GET, httpEntity, ServerResponse.class)
                        .getBody();

        List<LinkedHashMap<String, Object>> registrations = Objects.requireNonNull(serverResponse).getData();
        if (registrations != null && registrations.size() != 0) {
            services.clear();
            registrations.forEach(map -> {
                String serviceName = (String) map.get("serviceName");
                List<ServiceData> serviceData = services.get(serviceName);
                List<ServiceData> data = getServiceData((List<Map<String, Object>>) map.get("data"));
                if (serviceData == null) {
                    services.put(serviceName, new ArrayList<>(data));
                } else {
                    serviceData.addAll(data);
                }
            });
            log.trace("update service success");
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public String removeToken() {
        String result = this.token;
        this.token = "";
        return result;
    }

    @SuppressWarnings("all")
    private List<ServiceData> getServiceData(List<Map<String, Object>> data) {
        List<ServiceData> result = new ArrayList<>();
        data.forEach(serviceInfo -> {
            String url = (String) serviceInfo.get("url");
            Map<String, Object> machineInfo = (Map<String, Object>) serviceInfo.get("machineInfo");
            ServiceData serviceData = new ServiceData(url, new MachineInfo(machineInfo));
            result.add(serviceData);
        });

        return result;
    }

    /**
     * 根据服务名返回服务的 url
     *
     * @param serviceName 服务url
     * @return 返回服务集群中可用内存最高的 url
     */
    public String getServiceUrl(String serviceName) {
        return this.matcher.match(serviceName).getUrl();
    }
}
