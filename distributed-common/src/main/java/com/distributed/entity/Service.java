package com.distributed.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Ray
 * @date created in 2021/6/19 19:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {

    private String serviceName;
    private List<ServiceData> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ServiceData {
        private String url;
        private MachineInfo machineInfo;
    }
}
