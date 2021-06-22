package com.distributed.controller;

import com.distributed.MachineHolder;
import com.distributed.auth.AuthHolder;
import com.distributed.entity.*;
import com.distributed.registry.RegistryHolder;
import com.distributed.service.RegistryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ray
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/services")
public class RegistryController {

    private final RegistryService registryService;
    private final MachineHolder machineHolder;
    private final AuthHolder authHolder;

    @PostMapping
    public ServerResponse<Map<String, String>> register(@RequestBody Registration reg) {
        this.registryService.register(reg);
        log.info(String.format("Adding service: %s with URL: %s", reg.getServiceName(), reg.getServiceUrl()));
        Map<String, String> result = Map.of("token", authHolder.generateToken(reg.getServiceUrl()));
        return ServerResponse.success(result);
    }

    @DeleteMapping
    public <T> ServerResponse<T> unregister(@RequestBody RegistryHolder.UnregisterDto body) {
        String url = body.getUrl();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ServerResponse.error("url不符合要求");
        }

        this.registryService.unregister(url);
        this.authHolder.removeToken(url);
        log.info(String.format("Removing service at URL：%s", url));
        return ServerResponse.success("取消注册服务成功");
    }

    @GetMapping
    public ServerResponse<List<Service>> services() {
        List<Registration> registrationList = registryService.findAllServices();
        Map<String, List<ServiceData>> temp = new HashMap<>();

        registrationList.forEach(registration -> {

            String serviceName = registration.getServiceName();
            String url = registration.getServiceUrl();
            MachineInfo machineInfo = machineHolder.getMachineInfo(url);
            ServiceData data = new ServiceData(url, machineInfo);

            List<ServiceData> serviceData = temp.get(serviceName);
            if (serviceData == null) {
                temp.put(serviceName, new ArrayList<>(List.of(data)));
            } else {
                serviceData.add(data);
            }

        });

        List<Service> result = new ArrayList<>();
        temp.forEach((name, data) -> result.add(new Service(name, data)));

        return ServerResponse.success(result);
    }

}
