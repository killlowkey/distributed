package com.distributed.service.impl;

import com.distributed.entity.ServerResponse;
import com.distributed.exception.DistributedException;
import com.distributed.service.HeathService;
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
public class HealthServiceImpl implements HeathService {

    private ThreadPoolExecutor executor;
    private Map<String, HealthRunnable> healthData;
    private final RegistryService registryService;
    private final HealthProperties healthProperties;
    private final AtomicInteger integer = new AtomicInteger();

    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                r -> new Thread(r, "health-check-thread-" + integer.incrementAndGet()));
        healthData = new ConcurrentHashMap<>(64);
    }

    @Override
    public void addHeathCheck(String url) {
        HealthRunnable healthRunnable = new HealthRunnable(url, this.registryService, this.healthProperties);
        executor.submit(healthRunnable);
        healthData.put(url, healthRunnable);
    }

    @Override
    public void removeHeadCheck(String url) {
        HealthRunnable healthRunnable = healthData.get(url);
        if (healthRunnable == null) {
            throw new DistributedException(String.format("not found service with %s", url));
        }

        // 停止健康检查
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
        private Date resetDate = new Date();
        private boolean flag = true;
        private int count;

        public HealthRunnable(String url, RegistryService registryService,
                              HealthProperties healthProperties) {
            this.url = url;
            this.registryService = registryService;
            this.healthProperties = healthProperties;
        }

        @SneakyThrows
        @Override
        public void run() {
            while (flag) {
                if (!check()) {
                    count++;
                } else {
                    log.info(String.format("heath check success with %s", url));
                }

                if (count >= healthProperties.getFailedCount()) {
                    // 从注册中心移除服务
                    registryService.unregister(url);
                    log.info(String.format("Removing service at URL：%s", url));
                    break;
                }

                // 每隔 xS 重置 count
                if ((System.currentTimeMillis() - resetDate.getTime()) > healthProperties.getSecond() * 1000L) {
                    this.count = 0;
                }

                // 心跳检查间隔
                TimeUnit.SECONDS.sleep(healthProperties.getInterval());
            }
        }

        private boolean check() {
            try {
                ServerResponse serverResponse = restTemplate.getForObject(url + "/heath", ServerResponse.class);
                return serverResponse.getCode() == 200;
            } catch (Exception ex) {
                log.warn(String.format("heath check have a error：%s", ex.getMessage()));
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
