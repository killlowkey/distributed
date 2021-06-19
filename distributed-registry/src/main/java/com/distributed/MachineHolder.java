package com.distributed;

import com.distributed.entity.MachineInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ray
 * @date created in 2021/6/19 19:06
 */
@Component
@Slf4j
public class MachineHolder {

    private final Map<String, MachineInfo> machineInfoData =
            new ConcurrentHashMap<>(64);

    public void removeMachineInfo(String url) {
        this.machineInfoData.remove(url);
        log.info("remove {} machineInfo", url);
    }

    public void addOrUpdateMachineInfo(String url, MachineInfo machineInfo) {
        this.machineInfoData.put(url, machineInfo);
        log.info("add or update {} machineInfo", url);
    }

    public MachineInfo getMachineInfo(String url) {
        return this.machineInfoData.get(url);
    }
}
