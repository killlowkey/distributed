package com.distributed.service.impl;

import com.distributed.MachineHolder;
import com.distributed.auth.AuthHolder;
import com.distributed.entity.MachineInfo;
import com.distributed.entity.ServerResponse;
import com.distributed.exception.DistributedException;
import com.distributed.service.HealthService;
import com.distributed.service.RegistryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ray
 */
@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

    private ThreadPoolExecutor executor;
    private Map<String, HealthRunnable> healthData;
    private final RegistryService registryService;
    private final HealthProperties healthProperties;
    private final AtomicInteger integer = new AtomicInteger();
    private final MachineHolder machineHolder;
    private final AuthHolder authHolder;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                r -> new Thread(r, "health-check-thread-" + integer.incrementAndGet()));
        healthData = new ConcurrentHashMap<>(64);
    }

    @Override
    public void addHealthCheck(String url) {
        HealthRunnable healthRunnable = new HealthRunnable(url, this.registryService, this.healthProperties,
                machineHolder, authHolder);
        executor.submit(healthRunnable);
        healthData.put(url, healthRunnable);
    }

    @Override
    public void removeHealthCheck(String url) {
        HealthRunnable healthRunnable = healthData.get(url);
        if (healthRunnable == null) {
            throw new DistributedException(String.format("not found service with %s", url));
        }

        // ??????????????????
        healthRunnable.setFlag(false);
        healthData.remove(url);
    }

    @Data
    @Slf4j
    static class HealthRunnable implements Runnable {

        private final String url;
        private final RegistryService registryService;
        private final HealthProperties healthProperties;
        private final RestTemplate restTemplate = new RestTemplate();
        private final MachineHolder machineHolder;
        private AuthHolder authHolder;
        private Date resetDate = new Date();
        private boolean flag = true;
        private int count;

        public HealthRunnable(String url, RegistryService registryService,
                              HealthProperties healthProperties, MachineHolder machineHolder,
                              AuthHolder authHolder) {
            this.url = url;
            this.registryService = registryService;
            this.healthProperties = healthProperties;
            this.machineHolder = machineHolder;
            this.authHolder = authHolder;
        }

        @SneakyThrows
        @Override
        public void run() {
            while (flag) {
                if (!sendHealthCheck()) {
                    count++;
                } else {
                    log.info(String.format("health check success with %s", url));
                }

                if (count >= healthProperties.getFailedCount()) {
                    // ???????????????????????????
                    registryService.unregister(url);
                    // ??????????????????
                    machineHolder.removeMachineInfo(url);
                    // ??????token
                    authHolder.removeToken(url);
                    log.info(String.format("Removing service at URL???%s", url));
                    break;
                }

                // ?????? xS ?????? count
                if ((System.currentTimeMillis() - resetDate.getTime()) > healthProperties.getSecond() * 1000L) {
                    this.count = 0;
                }

                // ??????????????????
                TimeUnit.SECONDS.sleep(healthProperties.getInterval());
            }
        }

        @SuppressWarnings("all")
        private boolean sendHealthCheck() {
            try {
                ServerResponse<Map<String, Object>> serverResponse =
                        restTemplate.getForObject(url + "/health", ServerResponse.class);

                Map<String, Object> body = Objects.requireNonNull(serverResponse).getData();
                MachineInfo machineInfo = new MachineInfo(body);
                // ??????????????????
                machineHolder.addOrUpdateMachineInfo(url, machineInfo);

                return Objects.requireNonNull(serverResponse).getCode() == 200;
            } catch (Exception ex) {
                log.error(String.format("health check error???%s", ex.getMessage()));
                return false;
            }
        }

    }

    @Component
    @Data
    @ConfigurationProperties(prefix = "health")
    static class HealthProperties {
        private int failedCount;
        private int interval;
        private int second;
    }

}
