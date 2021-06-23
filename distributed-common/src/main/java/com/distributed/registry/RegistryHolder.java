package com.distributed.registry;

import com.distributed.annotation.ConditionalOnNotRegistry;
import com.distributed.entity.Registration;
import com.distributed.entity.ServerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Ray
 */
@Component
@ConditionalOnNotRegistry
@Slf4j
@RequiredArgsConstructor
public class RegistryHolder {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${server.port}")
    private int port;

    @Value("${registry.address}")
    private String registryAddress;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ServiceDiscovery serviceDiscovery;

    @SuppressWarnings("all")
    public void register() {
        Registration reg = new Registration(serviceName, String.format("http://localhost:%d", port));
        HttpEntity<Registration> httpEntity = createHttpEntity("", reg);

        ResponseEntity<ServerResponse> responseEntity = restTemplate.postForEntity(registryAddress + "/services",
                httpEntity, ServerResponse.class);
        ServerResponse serverResponse = responseEntity.getBody();

        if (responseEntity.getStatusCode() != HttpStatus.OK || serverResponse.getCode() != 200) {
            log.error(String.format("%s service register failed", serviceName));
            System.exit(0);
        }

        String token = ((Map<String, String>) serverResponse.getData()).get("token");
        log.info("token: {}", token);
        serviceDiscovery.setToken(token);

        log.info(String.format("success register %s service", serviceName));
    }

    @SuppressWarnings("all")
    public void unregister() {
        String url = String.format("http://localhost:%s", port);
        HttpEntity<UnregisterDto> httpEntity = createHttpEntity(serviceDiscovery.removeToken(), new UnregisterDto(url));

        ResponseEntity<ServerResponse> responseEntity = restTemplate.exchange("http://localhost:8000/services",
                HttpMethod.DELETE, httpEntity, ServerResponse.class);
        ServerResponse serverResponse = responseEntity.getBody();
        if (responseEntity.getStatusCode() != HttpStatus.OK || serverResponse.getCode() != 200) {
            log.error(String.format("failed unregister with %s", url));
        }

        log.info(String.format("success unregister with %s", url));
    }

    private <T> HttpEntity<T> createHttpEntity(String token, T body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("token", token);
        httpHeaders.set("url", "http://localhost:" + port);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, httpHeaders);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UnregisterDto {
        private String url;
    }
}
