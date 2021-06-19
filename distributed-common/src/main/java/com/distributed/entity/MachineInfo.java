package com.distributed.entity;

import cn.hutool.system.SystemUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * @author Ray
 * @date created in 2021/6/19 18:48
 */
@Data
@NoArgsConstructor
public class MachineInfo {

    // JVM 最大内存
    private long maxMemory;

    // JVM 已分配内存
    private long totalMemory;

    // JVM 已分配内存中的剩余空间
    private long freeMemory;

    // JVM最大可用内存
    private long usableMemory;

    // JVM 内存占用比（freeMemory / totalMemory）
    private double memoryRatio;

    public MachineInfo(Map<String, Object> info) {
        this.maxMemory = toLongValue(info.get("maxMemory"));
        this.totalMemory = toLongValue(info.get("totalMemory"));
        this.usableMemory = toLongValue(info.get("usableMemory"));
        this.freeMemory = toLongValue(info.get("freeMemory"));
        this.memoryRatio = (double) freeMemory / totalMemory;
    }

    private long toLongValue(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        } else if (value instanceof Long) {
            return (Long) value;
        }

        return -1L;
    }

    /**
     * 获取当前机器的信息
     *
     * @return MachineInfo 实例
     */
    public static MachineInfo getCurrentMachineInfo() {
        MachineInfo machineInfo = new MachineInfo();
        BeanUtils.copyProperties(SystemUtil.getRuntimeInfo(), machineInfo);
        machineInfo.setMemoryRatio((double) machineInfo.getFreeMemory() / machineInfo.getTotalMemory());
        return machineInfo;
    }
}
