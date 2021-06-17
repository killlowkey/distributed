package com.distributed.controller;

import com.distributed.entity.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务心跳
 *
 * @author Ray
 */
@RestController
public class HeathController {

    @GetMapping("/health")
    public <T> ServerResponse<T> health() {
        return ServerResponse.success("success");
    }

}
