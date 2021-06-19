package com.distributed.health;

import com.distributed.entity.MachineInfo;
import com.distributed.entity.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务心跳：返回机器状态
 *
 * @author Ray
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public ServerResponse<MachineInfo> health() {
        return ServerResponse.success(MachineInfo.getCurrentMachineInfo());
    }

}
