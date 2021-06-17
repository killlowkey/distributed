package com.distributed.controller;

import com.distributed.entity.LogDto;
import com.distributed.entity.Order;
import com.distributed.entity.ServerResponse;
import com.distributed.registry.ServiceDiscovery;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 * @author Ray
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final ServiceDiscovery serviceDiscovery;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.application.name}")
    private String serviceName;

    @PostMapping("/order")
    public ServerResponse<Order> placeOrder(@RequestBody OrderDto orderDto) {
        Order order = new Order(new Random().nextInt(Integer.MAX_VALUE), orderDto.getName(), 34.65f);
        // 远程调用 Log 服务
        String logServiceUrl = serviceDiscovery.getService("Log-Service");
        LogDto logDto = new LogDto(serviceName, String.format("订单[%s]下单成功", order.getId()));
        restTemplate.postForObject(logServiceUrl + "/log", createHttpEntity(logDto), ServerResponse.class);
        return ServerResponse.success(order);
    }

    private <T> HttpEntity<T> createHttpEntity(T body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, httpHeaders);
    }

    @Data
    static class OrderDto {
        private String name;
    }
}
