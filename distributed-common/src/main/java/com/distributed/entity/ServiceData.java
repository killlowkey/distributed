package com.distributed.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ray
 * @date created in 2021/6/20 9:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceData {
    private String url;
    private MachineInfo machineInfo;
}
