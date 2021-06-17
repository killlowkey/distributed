package com.distributed.controller;

import com.distributed.entity.Registration;
import com.distributed.registry.RegistryHolder;
import com.distributed.entity.ServerResponse;
import com.distributed.service.RegistryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Ray
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/services")
public class RegistryController {

    private final RegistryService registryService;

    @PostMapping
    public <T> ServerResponse<T> register(@RequestBody Registration reg) {
        registryService.register(reg);
        log.info(String.format("Adding service: %s with URL: %s", reg.getServiceName(), reg.getServiceUrl()));
        return ServerResponse.success(reg.getServiceName() + " 服务注册成功");
    }

    @DeleteMapping
    public <T> ServerResponse<T> unregister(@RequestBody RegistryHolder.UnregisterDto body) {
        String url = body.getUrl();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ServerResponse.error("url不符合要求");
        }

        registryService.unregister(url);
        log.info(String.format("Removing service at URL：%s", url));
        return ServerResponse.success("取消注册服务成功");
    }

    @GetMapping
    public ServerResponse<List<Registration>> services() {
        return ServerResponse.success(registryService.findAllServices());
    }

}
