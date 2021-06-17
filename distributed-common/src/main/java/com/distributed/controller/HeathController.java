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

    @GetMapping("/heath")
    public <T> ServerResponse<T> heath() {
        return ServerResponse.success("success");
    }

}
